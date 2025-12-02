package com.backendsems.SEMS.application.internal.queryservices;

import com.backendsems.SEMS.domain.model.entities.DeviceConsumptionEntity;
import com.backendsems.SEMS.domain.model.entities.PreferencesEntity;
import com.backendsems.SEMS.domain.model.queries.GetAllPreferencesByUserIdQuery;
import com.backendsems.SEMS.domain.model.queries.GetDevicesByUserIdQuery;
import com.backendsems.SEMS.domain.model.queries.GetDashboardByUserIdQuery;
import com.backendsems.SEMS.domain.model.queries.GetTopDevicesByUserQuery;
import com.backendsems.SEMS.domain.model.queries.GetWeeklyConsumptionByUserQuery;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.domain.services.DashboardQueryService;
import com.backendsems.SEMS.domain.services.DeviceQueryService;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.DashboardRepository;
import com.backendsems.SEMS.domain.model.aggregates.Dashboard;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardQueryServiceImpl implements DashboardQueryService {

    private final DeviceQueryService deviceQueryService;
    private final DashboardRepository dashboardRepository;

    public DashboardQueryServiceImpl(DeviceQueryService deviceQueryService,
                                     DashboardRepository dashboardRepository) {
        this.deviceQueryService = deviceQueryService;
        this.dashboardRepository = dashboardRepository;
    }

    @Override
    public GetDashboardByUserIdQuery.DashboardData handle(GetDashboardByUserIdQuery query) {
        UserId userId = query.userId();

        var deviceAggregates = deviceQueryService.handle(new GetDevicesByUserIdQuery(userId));
        var devices = deviceAggregates.stream()
                .map(d -> new GetDashboardByUserIdQuery.DeviceItem(
                        extractId(d),
                        extractName(d),
                        extractCategoryString(d)
                ))
                .collect(Collectors.toList());
        int activeDevices = devices.size();

        var weekly = deviceQueryService.handle(new GetWeeklyConsumptionByUserQuery(userId));
        Instant now = Instant.now();
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        Instant startOfDay = today.atStartOfDay().toInstant(ZoneOffset.UTC);

        Map<Integer, Double> hourly = new HashMap<>();
        for (int h = 0; h < 24; h++) hourly.put(h, 0.0);
        weekly.stream()
                .filter(c -> {
                    Instant ts = extractTimestamp(c);
                    return ts != null && !ts.isBefore(startOfDay) && !ts.isAfter(now);
                })
                .forEach(c -> {
                    Instant ts = extractTimestamp(c);
                    if (ts != null) {
                        int hour = ZonedDateTime.ofInstant(ts, ZoneOffset.UTC).getHour();
                        double kwh = extractKwh(c);
                        hourly.compute(hour, (k, v) -> v + kwh);
                    }
                });

        var daily = hourly.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new GetDashboardByUserIdQuery.DailyItem(
                        today.atTime(e.getKey(), 0).toInstant(ZoneOffset.UTC),
                        e.getValue()))
                .collect(Collectors.toList());

        double todaysConsumptionKwh = daily.stream()
                .mapToDouble(GetDashboardByUserIdQuery.DailyItem::kwh).sum();


        var topDevices = deviceQueryService.handle(new GetTopDevicesByUserQuery(userId, 10));
        Map<String, Double> categoryConsumption = topDevices.stream()
                .collect(Collectors.groupingBy(
                        c -> {
                            String cat = extractDeviceCategory(topDevices, c);
                            return cat != null ? cat : "Other";
                        },
                        Collectors.summingDouble(this::extractKwh)));


        var preferences = deviceQueryService.handle(new GetAllPreferencesByUserIdQuery(userId));
        double monthlySavingGoalKwh = preferences.stream()
                .map(this::extractMonthlyGoal)
                .filter(v -> v > 0)
                .findFirst().orElse(0.0);
        double pricePerKwh = preferences.stream()
                .map(this::extractPricePerKwh)
                .filter(v -> v > 0)
                .findFirst().orElse(0.0);


        YearMonth currentMonth = YearMonth.now(ZoneOffset.UTC);
        double currentMonthConsumption = weekly.stream()
                .filter(c -> {
                    Instant ts = extractTimestamp(c);
                    return ts != null && YearMonth.from(ZonedDateTime.ofInstant(ts, ZoneOffset.UTC)).equals(currentMonth);
                })
                .mapToDouble(this::extractKwh)
                .sum();

        double estimatedSavingsPercent = monthlySavingGoalKwh > 0
                ? ((monthlySavingGoalKwh - currentMonthConsumption) / monthlySavingGoalKwh) * 100.0
                : 0.0;

        double estimatedBill = todaysConsumptionKwh * pricePerKwh;


        List<GetDashboardByUserIdQuery.AlertItem> alerts = new ArrayList<>();
        Instant threeHoursAgo = now.minus(3, ChronoUnit.HOURS);
        double last3Hours = weekly.stream()
                .filter(c -> {
                    Instant ts = extractTimestamp(c);
                    return ts != null && !ts.isBefore(threeHoursAgo) && !ts.isAfter(now);
                })
                .mapToDouble(this::extractKwh)
                .sum();
        long hoursElapsed = Math.max(1, Duration.between(startOfDay, now).toHours());
        double avgSoFarPerHour = todaysConsumptionKwh / hoursElapsed;
        if (avgSoFarPerHour > 0 && last3Hours > avgSoFarPerHour * 3 * 1.2) {
            alerts.add(new GetDashboardByUserIdQuery.AlertItem(
                    "warning",
                    "High consumption detected! Your usage is 20% above average in the last 3 hours."
            ));
        }


        preferences.forEach(p -> {
            int autoOffHour = extractAutoOffHour(p);
            if (autoOffHour >= 0) {
                int currentHour = ZonedDateTime.ofInstant(now, ZoneOffset.UTC).getHour();
                if (currentHour > autoOffHour) {
                    alerts.add(new GetDashboardByUserIdQuery.AlertItem(
                            "info",
                            "Reminder: You forgot to turn off a device past its scheduled off hour."
                    ));
                }
            }
        });

        // Persistencia en tabla dashboard
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

    private Instant extractTimestamp(DeviceConsumptionEntity e) {
        Object v = invokeFirst(e,
                "getTimestamp", "getOccurredAt", "getRecordedAt", "getCreatedAt", "getDateTime", "getDate");
        if (v instanceof Instant) return (Instant) v;
        if (v instanceof LocalDateTime) return ((LocalDateTime) v).toInstant(ZoneOffset.UTC);
        if (v instanceof LocalDate) return ((LocalDate) v).atStartOfDay().toInstant(ZoneOffset.UTC);
        if (v instanceof Date) return ((Date) v).toInstant();
        return null;
    }

    private double extractKwh(DeviceConsumptionEntity e) {
        Object v = invokeFirst(e,
                "getKwh", "getConsumption", "getEnergy", "getValue", "getAmount");
        if (v instanceof Number) return ((Number) v).doubleValue();
        return 0.0;
    }

    private String extractDeviceCategory(List<DeviceConsumptionEntity> list, DeviceConsumptionEntity e) {
        Object v = invokeFirst(e,
                "getDeviceCategory", "getCategory", "deviceCategory", "category");
        return v != null ? v.toString() : null;
    }

    private double extractMonthlyGoal(PreferencesEntity p) {
        Object v = invokeFirst(p,
                "getMonthlySavingGoalKwh", "getMonthlySavingGoal", "getSavingGoal", "getGoalKwh", "getGoal");
        if (v instanceof Number) return ((Number) v).doubleValue();
        return 0.0;
    }

    private double extractPricePerKwh(PreferencesEntity p) {
        Object v = invokeFirst(p,
                "getPricePerKwh", "getEnergyPrice", "getTariff", "getPrice");
        if (v instanceof Number) return ((Number) v).doubleValue();
        return 0.0;
    }

    private int extractAutoOffHour(PreferencesEntity p) {
        Object v = invokeFirst(p,
                "getAutoOffHour", "getTurnOffHour", "getOffHour", "getScheduledOffHour");
        if (v instanceof Number) return ((Number) v).intValue();
        return -1;
    }

    private Object invokeFirst(Object target, String... methodNames) {
        for (String m : methodNames) {
            try {
                Method method = target.getClass().getMethod(m);
                return method.invoke(target);
            } catch (Exception ignored) {}
        }
        return null;
    }
}
