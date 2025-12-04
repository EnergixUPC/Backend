package com.backendsems.SEMS.application.internal.queryservices;

import com.backendsems.SEMS.domain.model.entities.DeviceConsumption;
import com.backendsems.SEMS.domain.model.entities.DevicePreference;
import com.backendsems.SEMS.domain.model.queries.GetAllPreferencesByUserIdQuery;
import com.backendsems.SEMS.domain.model.queries.GetDevicesByUserIdQuery;
import com.backendsems.SEMS.domain.model.queries.GetDashboardByUserIdQuery;
import com.backendsems.SEMS.domain.model.queries.GetWeeklyConsumptionByUserQuery;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.domain.services.DashboardQueryService;
import com.backendsems.SEMS.domain.services.DeviceQueryService;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.DashboardRepository;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.DeviceConsumptionRepository;
import com.backendsems.SEMS.domain.model.aggregates.Dashboard;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardQueryServiceImpl implements DashboardQueryService {

    private final DeviceQueryService deviceQueryService;
    private final DashboardRepository dashboardRepository;
    private final DeviceConsumptionRepository deviceConsumptionRepository;

    public DashboardQueryServiceImpl(DeviceQueryService deviceQueryService,
                                     DashboardRepository dashboardRepository,
                                     DeviceConsumptionRepository deviceConsumptionRepository) {
        this.deviceQueryService = deviceQueryService;
        this.dashboardRepository = dashboardRepository;
        this.deviceConsumptionRepository = deviceConsumptionRepository;
    }

    @Override
    public GetDashboardByUserIdQuery.DashboardData handle(GetDashboardByUserIdQuery query) {
        UserId userId = query.userId();

        // 1. Obtener dispositivos activos del usuario
        var deviceAggregates = deviceQueryService.handle(new GetDevicesByUserIdQuery(userId));
        var devices = deviceAggregates.stream()
                .map(d -> new GetDashboardByUserIdQuery.DeviceItem(
                        extractId(d),
                        extractName(d),
                        extractCategoryString(d)
                ))
                .collect(Collectors.toList());
        int activeDevices = devices.size();

        // 2. Obtener fecha actual
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        
        // 3. Calcular consumo del día actual directamente desde el repositorio (suma de todos los dispositivos)
        Double todaysConsumptionKwhRaw = deviceConsumptionRepository.sumDailyConsumptionByUserIdAndDate(userId.id(), today);
        double todaysConsumptionKwh = todaysConsumptionKwhRaw != null ? todaysConsumptionKwhRaw : 0.0;
        
        // 4. Calcular consumo mensual total (suma de todos los dispositivos del usuario)
        Double currentMonthConsumptionRaw = deviceConsumptionRepository.sumMonthlyConsumptionByUserId(userId.id());
        double currentMonthConsumption = currentMonthConsumptionRaw != null ? currentMonthConsumptionRaw : 0.0;
        
        // 5. Obtener todos los consumos del usuario para cálculos adicionales
        var allConsumptions = deviceQueryService.handle(new GetWeeklyConsumptionByUserQuery(userId));
        List<DeviceConsumption> todayConsumptions = allConsumptions.stream()
                .filter(c -> c.getPeriodo().equals("daily") && 
                            c.getFecha() != null && 
                            c.getFecha().equals(today))
                .collect(Collectors.toList());

        // 6. Configurar precio por kWh y meta mensual (valores por defecto o de preferencias)
        var preferences = deviceQueryService.handle(new GetAllPreferencesByUserIdQuery(userId.id()));
        double pricePerKwh = 0.50; // Precio por defecto en soles por kWh (Perú)
        double monthlySavingGoalKwh = 300.0; // Meta de ahorro por defecto: 300 kWh mensuales

        // Intentar obtener threshold promedio como referencia de meta
        if (!preferences.isEmpty()) {
            double avgThreshold = preferences.stream()
                    .mapToDouble(DevicePreference::getThreshold)
                    .average()
                    .orElse(300.0);
            if (avgThreshold > 0) {
                monthlySavingGoalKwh = avgThreshold;
            }
        }

        // 7. Calcular factura estimada mensual
        double estimatedBill = currentMonthConsumption * pricePerKwh;

        // 8. Calcular porcentaje de ahorro estimado
        // Porcentaje de ahorro = ((Meta - Consumo actual) / Meta) * 100
        // Si el consumo es menor que la meta, hay ahorro positivo
        // Si el consumo es mayor que la meta, hay ahorro negativo (se está pasando)
        double estimatedSavingsPercent = 0.0;
        if (monthlySavingGoalKwh > 0) {
            estimatedSavingsPercent = ((monthlySavingGoalKwh - currentMonthConsumption) / monthlySavingGoalKwh) * 100.0;
        }

        // 9. Preparar datos por hora para gráficos (consumo del día actual)
        Map<Integer, Double> hourly = new HashMap<>();
        for (int h = 0; h < 24; h++) hourly.put(h, 0.0);
        
        // Distribuir el consumo diario a lo largo de las horas (simulación simple)
        int currentHour = ZonedDateTime.now(ZoneOffset.UTC).getHour();
        todayConsumptions.forEach(c -> {
            double consumoPerHour = c.getConsumo() / 24.0;
            for (int h = 0; h <= currentHour; h++) {
                hourly.compute(h, (k, v) -> v + consumoPerHour);
            }
        });

        var daily = hourly.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new GetDashboardByUserIdQuery.DailyItem(
                        today.atTime(e.getKey(), 0).toInstant(ZoneOffset.UTC),
                        e.getValue()))
                .collect(Collectors.toList());

        // 10. Calcular consumo por categoría
        Map<String, Double> categoryConsumption = new HashMap<>();
        
        // Agrupar consumos por categoría de dispositivo
        deviceAggregates.forEach(device -> {
            Long deviceId = extractId(device);
            String category = extractCategoryString(device);
            
            double deviceDailyConsumption = todayConsumptions.stream()
                    .filter(c -> c.getDevice().getId().equals(deviceId))
                    .mapToDouble(DeviceConsumption::getConsumo)
                    .sum();
            
            categoryConsumption.merge(category != null ? category : "Other", 
                                     deviceDailyConsumption, 
                                     Double::sum);
        });

        // 11. Generar alertas inteligentes
        List<GetDashboardByUserIdQuery.AlertItem> alerts = new ArrayList<>();
        
        // Alerta si el consumo del mes actual supera el 80% de la meta
        if (monthlySavingGoalKwh > 0 && currentMonthConsumption > monthlySavingGoalKwh * 0.8) {
            alerts.add(new GetDashboardByUserIdQuery.AlertItem(
                    "warning",
                    String.format("¡Atención! Has consumido %.1f kWh de tu meta de %.1f kWh (%.1f%%)",
                            currentMonthConsumption, monthlySavingGoalKwh, 
                            (currentMonthConsumption / monthlySavingGoalKwh) * 100)
            ));
        }

        // Alerta si el consumo diario es muy alto
        double avgDailyConsumption = monthlySavingGoalKwh / 30.0; // Promedio esperado por día
        if (todaysConsumptionKwh > avgDailyConsumption * 1.5) {
            alerts.add(new GetDashboardByUserIdQuery.AlertItem(
                    "info",
                    String.format("Consumo alto hoy: %.2f kWh (promedio esperado: %.2f kWh)",
                            todaysConsumptionKwh, avgDailyConsumption)
            ));
        }

        // Alerta positiva si estás ahorrando
        if (estimatedSavingsPercent > 20) {
            alerts.add(new GetDashboardByUserIdQuery.AlertItem(
                    "success",
                    String.format("¡Excelente! Estás ahorrando un %.1f%% respecto a tu meta mensual",
                            estimatedSavingsPercent)
            ));
        }

        // 12. Persistir en tabla dashboard
        Optional<Dashboard> existing = dashboardRepository.findByUserId(userId.id());
        if (existing.isPresent()) {
            Dashboard dashboard = existing.get();
            dashboard.updateMetrics(
                    monthlySavingGoalKwh,
                    estimatedSavingsPercent,
                    activeDevices,
                    estimatedBill,
                    todaysConsumptionKwh
            );
            dashboardRepository.save(dashboard);
        } else {
            Dashboard dashboard = new Dashboard(
                    userId.id(),
                    monthlySavingGoalKwh,
                    estimatedSavingsPercent,
                    activeDevices,
                    estimatedBill,
                    todaysConsumptionKwh,
                    "S/."
            );
            dashboardRepository.save(dashboard);
        }

        return new GetDashboardByUserIdQuery.DashboardData(
                monthlySavingGoalKwh,
                estimatedSavingsPercent,
                activeDevices,
                estimatedBill,
                todaysConsumptionKwh,
                daily,
                categoryConsumption,
                devices,
                alerts
        );
    }


    private Long extractId(Object device) {
        Object v = invokeFirst(device,
                "getId", "id");
        if (v instanceof Long) return (Long) v;
        return null;
    }

    private String extractName(Object device) {
        Object v = invokeFirst(device,
                "getName", "getDeviceName", "name", "deviceName");
        return v != null ? v.toString() : "Unknown";
    }

    private String extractCategoryString(Object device) {
        Object v = invokeFirst(device,
                "getCategory", "getDeviceCategory", "category", "deviceCategory");
        return v != null ? v.toString() : "Unknown";
    }

    // Métodos auxiliares para extraer datos - Comentados ya que no están en uso actualmente
    // Se mantienen para futuras implementaciones

    /*
    private Instant extractTimestamp(DeviceConsumption e) {
        Object v = invokeFirst(e,
                "getTimestamp", "getOccurredAt", "getRecordedAt", "getCreatedAt", "getDateTime", "getDate");
        if (v instanceof Instant instant) return instant;
        if (v instanceof LocalDateTime ldt) return ldt.toInstant(ZoneOffset.UTC);
        if (v instanceof LocalDate ld) return ld.atStartOfDay().toInstant(ZoneOffset.UTC);
        if (v instanceof Date date) return date.toInstant();
        return null;
    }

    private double extractKwh(DeviceConsumption e) {
        Object v = invokeFirst(e,
                "getKwh", "getConsumption", "getEnergy", "getValue", "getAmount");
        if (v instanceof Number number) return number.doubleValue();
        return 0.0;
    }

    private String extractDeviceCategory(List<DeviceConsumption> list, DeviceConsumption e) {
        Object v = invokeFirst(e,
                "getDeviceCategory", "getCategory", "deviceCategory", "category");
        return v != null ? v.toString() : null;
    }

    private double extractMonthlyGoal(DevicePreference p) {
        Object v = invokeFirst(p,
                "getMonthlySavingGoalKwh", "getMonthlySavingGoal", "getSavingGoal", "getGoalKwh", "getGoal");
        if (v instanceof Number number) return number.doubleValue();
        return 0.0;
    }

    private double extractPricePerKwh(DevicePreference p) {
        Object v = invokeFirst(p,
                "getPricePerKwh", "getEnergyPrice", "getTariff", "getPrice");
        if (v instanceof Number number) return number.doubleValue();
        return 0.0;
    }

    private int extractAutoOffHour(DevicePreference p) {
        Object v = invokeFirst(p,
                "getAutoOffHour", "getTurnOffHour", "getOffHour", "getScheduledOffHour");
        if (v instanceof Number number) return number.intValue();
        return -1;
    }
    */

    private Object invokeFirst(Object target, String... methodNames) {
        for (String m : methodNames) {
            try {
                Method method = target.getClass().getMethod(m);
                return method.invoke(target);
            } catch (Exception ignored) {
                // Ignorar excepciones de métodos no encontrados
            }
        }
        return null;
    }
}
