package com.backendsems.SEMS.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.List;

public record UserWeeklyConsumptionResource(
        List<DailyConsumptionData> dailyConsumptions,
        List<DeviceWeeklyTotal> deviceTotals,
        Double totalWeeklyConsumptionKwh,
        LocalDate weekStart,
        LocalDate weekEnd
) {
    public record DailyConsumptionData(
            LocalDate date,
            String dayName,
            Double consumptionKwh
    ) {}

    public record DeviceWeeklyTotal(
            String deviceId,
            String deviceName,
            Double weeklyConsumptionKwh
    ) {}
}