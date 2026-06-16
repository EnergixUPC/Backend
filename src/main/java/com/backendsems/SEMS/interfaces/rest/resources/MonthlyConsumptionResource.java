package com.backendsems.SEMS.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.List;

public record MonthlyConsumptionResource(
    List<DailyConsumptionData> dailyConsumptions,
    Double totalMonthlyConsumption,
    LocalDate monthStartDate,
    LocalDate monthEndDate
) {
    public record DailyConsumptionData(
        LocalDate date,
        Double consumption
    ) {}
}
