package com.backendsems.SEMS.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.List;

public record CompareConsumptionResource(
    PeriodConsumptionData period1,
    PeriodConsumptionData period2,
    Double difference,
    Double percentageDifference
) {
    public record PeriodConsumptionData(
        LocalDate startDate,
        LocalDate endDate,
        Double totalConsumption,
        List<DailyConsumptionData> dailyConsumptions
    ) {}

    public record DailyConsumptionData(
        LocalDate date,
        Double consumption
    ) {}
}
