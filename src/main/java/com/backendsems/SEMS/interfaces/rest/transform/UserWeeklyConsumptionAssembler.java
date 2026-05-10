package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.aggregates.Device;
import com.backendsems.SEMS.domain.model.entities.Consumption;
import com.backendsems.SEMS.interfaces.rest.resources.UserWeeklyConsumptionResource;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class UserWeeklyConsumptionAssembler {

    // kW·min stored in DB → divide by 60 to get kWh
    private static final double MINUTES_PER_HOUR = 60.0;

    public static UserWeeklyConsumptionResource toResource(
            List<Consumption> consumptions,
            List<Device> devices,
            LocalDate weekStart,
            LocalDate weekEnd) {

        Map<LocalDate, Double> dailyTotalsKwh = consumptions.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getCalculatedAt().toLocalDate(),
                        Collectors.summingDouble(c -> c.getConsumption() / MINUTES_PER_HOUR)
                ));

        List<UserWeeklyConsumptionResource.DailyConsumptionData> dailyData = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            double kwh = round2(dailyTotalsKwh.getOrDefault(date, 0.0));
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
            dailyData.add(new UserWeeklyConsumptionResource.DailyConsumptionData(date, dayName, kwh));
        }

        Map<String, String> deviceNames = devices.stream()
                .collect(Collectors.toMap(
                        d -> String.valueOf(d.getId()),
                        d -> d.getName().name()
                ));

        Map<String, Double> deviceTotalsKwh = consumptions.stream()
                .collect(Collectors.groupingBy(
                        Consumption::getDeviceId,
                        Collectors.summingDouble(c -> c.getConsumption() / MINUTES_PER_HOUR)
                ));

        List<UserWeeklyConsumptionResource.DeviceWeeklyTotal> deviceTotals = deviceTotalsKwh.entrySet().stream()
                .map(e -> new UserWeeklyConsumptionResource.DeviceWeeklyTotal(
                        e.getKey(),
                        deviceNames.getOrDefault(e.getKey(), "Unknown"),
                        round2(e.getValue())
                ))
                .collect(Collectors.toList());

        double total = round2(consumptions.stream()
                .mapToDouble(c -> c.getConsumption() / MINUTES_PER_HOUR)
                .sum());

        return new UserWeeklyConsumptionResource(dailyData, deviceTotals, total, weekStart, weekEnd);
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}