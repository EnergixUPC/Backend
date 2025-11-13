package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.entities.Device;
import com.backendsems.SEMS.domain.model.entities.WeeklyConsumption;
import com.backendsems.SEMS.domain.model.entities.WeeklyConsumptionData;
import com.backendsems.SEMS.infrastructure.repositories.DeviceRepository;
import com.backendsems.SEMS.infrastructure.repositories.WeeklyConsumptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ReportsService
 * Servicio para el manejo de reportes de consumo energético
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportsService {

    private final WeeklyConsumptionRepository weeklyConsumptionRepository;
    private final DeviceRepository deviceRepository;

    /**
     * Obtiene el consumo semanal de un usuario
     */
    public List<WeeklyConsumption> getWeeklyConsumption(Long userId) {
        return weeklyConsumptionRepository.findByUserIdOrderByDateDesc(userId);
    }

    /**
     * Obtiene el consumo semanal de un usuario para un año específico
     */
    public List<WeeklyConsumption> getWeeklyConsumptionByYear(Long userId, Integer year) {
        return weeklyConsumptionRepository.findByUserIdAndYear(userId, year.toString());
    }

    /**
     * Obtiene el consumo semanal más reciente (últimas N semanas)
     */
    public List<WeeklyConsumption> getRecentWeeklyConsumption(Long userId, Integer weeks) {
        return weeklyConsumptionRepository.findRecentWeeklyConsumption(userId, weeks);
    }

    /**
     * Obtiene el consumo semanal por rango de fechas
     */
    public List<WeeklyConsumption> getWeeklyConsumptionByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return weeklyConsumptionRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    /**
     * Obtiene estadísticas del consumo semanal para el dashboard
     */
    public Map<String, Object> getWeeklyConsumptionStats(Long userId) {
        int currentYear = LocalDate.now().getYear();
        
        // Obtener estadísticas básicas
        Double averageWeekly = weeklyConsumptionRepository.getAverageWeeklyConsumption(userId, String.valueOf(currentYear));
        Double totalYearly = weeklyConsumptionRepository.getTotalYearlyConsumption(userId, String.valueOf(currentYear));
        Optional<WeeklyConsumption> peakWeek = weeklyConsumptionRepository.findPeakWeekByUserId(userId);
        
        // Obtener últimas 8 semanas para el gráfico
        List<WeeklyConsumption> recentWeeks = getRecentWeeklyConsumption(userId, 8);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("averageWeeklyConsumption", averageWeekly != null ? averageWeekly : 0.0);
        stats.put("totalYearlyConsumption", totalYearly != null ? totalYearly : 0.0);
        stats.put("peakWeekConsumption", peakWeek.map(WeeklyConsumption::getTotalConsumption).orElse(0.0));
        stats.put("peakWeekDate", peakWeek.map(w -> w.getStartDate().toString()).orElse(""));
        stats.put("recentWeeks", recentWeeks);
        stats.put("year", currentYear);
        
        return stats;
    }

    /**
     * Obtiene el ranking de dispositivos por consumo para un período
     */
    public List<Map<String, Object>> getDeviceRanking(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Device> devices = deviceRepository.findByUserId(userId);
        
        return devices.stream()
                .map(device -> {
                    Map<String, Object> deviceData = new HashMap<>();
                    deviceData.put("id", device.getId());
                    deviceData.put("name", device.getName());
                    deviceData.put("type", device.getType().toString());
                    deviceData.put("totalConsumption", device.getConsumptionKwh());
                    deviceData.put("isActive", device.getIsActive());
                    return deviceData;
                })
                .sorted((d1, d2) -> Double.compare(
                        (Double) d2.get("totalConsumption"), 
                        (Double) d1.get("totalConsumption")))
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo registro de consumo semanal
     */
    @Transactional
    public WeeklyConsumption createWeeklyConsumption(Long userId, LocalDate weekStartDate) {
        LocalDate weekEndDate = weekStartDate.plusDays(6);
        int weekNumber = weekStartDate.get(WeekFields.ISO.weekOfYear());
        int year = weekStartDate.getYear();
        String week = String.format("%d-W%02d", year, weekNumber);

        // Crear WeeklyConsumption sin User (se manejará por userId)
        WeeklyConsumption weeklyConsumption = new WeeklyConsumption();
        weeklyConsumption.setStartDate(weekStartDate);
        weeklyConsumption.setEndDate(weekEndDate);
        weeklyConsumption.setWeek(week);
        weeklyConsumption.setDataPoints(new ArrayList<>());
        
        // Establecer el userId directamente sin crear User entity
        weeklyConsumption.setUserId(userId);

        // Crear datos para cada día de la semana en formato db.json
        String[] days = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = weekStartDate.plusDays(i);
            
            WeeklyConsumptionData dailyData = WeeklyConsumptionData.builder()
                    .day(days[i])
                    .date(currentDate)
                    .consumption(0.0)
                    .efficiency(85)
                    .trend("stable")
                    .weeklyConsumption(weeklyConsumption)
                    .build();
            
            weeklyConsumption.getDataPoints().add(dailyData);
        }

        return weeklyConsumptionRepository.save(weeklyConsumption);
    }

    /**
     * Actualiza el consumo semanal con datos reales
     */
    @Transactional
    public WeeklyConsumption updateWeeklyConsumption(Long userId, Integer year, Integer weekNumber, 
                                                     Map<String, Double> dailyConsumptions) {
        String weekString = String.format("%d-W%02d", year, weekNumber);
        WeeklyConsumption weeklyConsumption = weeklyConsumptionRepository
                .findByUserIdAndYearAndWeek(userId, weekString)
                .orElseThrow(() -> new RuntimeException("Registro de consumo semanal no encontrado"));

        // Actualizar datos diarios
        weeklyConsumption.getDataPoints().forEach(dailyData -> {
            String dayKey = dailyData.getDay().toLowerCase();
            Double consumption = dailyConsumptions.get(dayKey);
            if (consumption != null) {
                dailyData.setConsumption(consumption);
            }
        });

        // Recalcular totales y estadísticas
        weeklyConsumption.calculateTotalConsumption();
        weeklyConsumption.calculateAverageConsumption();
        weeklyConsumption.findPeakDay();

        return weeklyConsumptionRepository.save(weeklyConsumption);
    }

    /**
     * Genera datos de consumo semanal de ejemplo para testing
     * Utiliza la estructura del db.json pero con datos realistas generados
     */
    @Transactional
    public void generateSampleWeeklyData(Long userId) {
        try {
            System.out.println("DEBUG: Iniciando generateSampleWeeklyData para userId: " + userId);
            
            // Crear datos para la semana actual
            LocalDate weekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
            System.out.println("DEBUG: Semana inicio: " + weekStart);
            
            WeeklyConsumption weeklyConsumption = createWeeklyConsumption(userId, weekStart);
            System.out.println("DEBUG: WeeklyConsumption creado: " + weeklyConsumption.getId());
            
            // Generar datos realistas (no hardcodeados)
            java.util.Random random = new java.util.Random();
            double baseConsumption = 30.0 + (random.nextDouble() * 20.0); // Entre 30-50 kWh base
            
            String[] trendOptions = {"up", "down", "stable"};
            
            for (int i = 0; i < weeklyConsumption.getDataPoints().size(); i++) {
                WeeklyConsumptionData dataPoint = weeklyConsumption.getDataPoints().get(i);
                
                // Generar consumo con variación natural
                double dailyVariation = -10.0 + (random.nextDouble() * 20.0); // ±10 kWh de variación
                double consumption = Math.max(15.0, baseConsumption + dailyVariation);
                
                // Los fines de semana tienden a tener mayor consumo
                if (i >= 5) { // SAT, SUN
                    consumption += 5.0 + (random.nextDouble() * 15.0);
                }
                
                // Generar eficiencia realista (entre 60-90%)
                int efficiency = 60 + random.nextInt(31);
                
                // Generar tendencia
                String trend = trendOptions[random.nextInt(trendOptions.length)];
                
                dataPoint.setConsumption(Math.round(consumption * 10.0) / 10.0); // 1 decimal
                dataPoint.setEfficiency(efficiency);
                dataPoint.setTrend(trend);
            }
            
            // Calcular totales y estadísticas
            weeklyConsumption.calculateTotalConsumption();
            weeklyConsumption.calculateAverageConsumption();
            weeklyConsumption.findPeakDay();
            
            // Calcular promedio semanal basado en los datos generados
            weeklyConsumption.setWeeklyAverage(weeklyConsumption.getAverageConsumption());
            
            System.out.println("DEBUG: Guardando en repositorio");
            weeklyConsumptionRepository.save(weeklyConsumption);
            System.out.println("DEBUG: WeeklyConsumption guardado exitosamente");
            
        } catch (Exception e) {
            System.out.println("ERROR en generateSampleWeeklyData: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Calcula el porcentaje de cambio entre dos semanas
     */
    public Double calculateWeeklyChangePercentage(Long userId) {
        List<WeeklyConsumption> recentWeeks = getRecentWeeklyConsumption(userId, 2);
        
        if (recentWeeks.size() >= 2) {
            Double currentWeek = recentWeeks.get(0).getTotalConsumption();
            Double previousWeek = recentWeeks.get(1).getTotalConsumption();
            
            if (previousWeek != null && previousWeek > 0) {
                return ((currentWeek - previousWeek) / previousWeek) * 100;
            }
        }
        
        return 0.0;
    }
}