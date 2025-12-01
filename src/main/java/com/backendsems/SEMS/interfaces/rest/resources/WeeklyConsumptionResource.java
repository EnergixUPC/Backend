package com.backendsems.SEMS.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.List;

/**
 * WeeklyConsumptionResource
 * Recurso REST para representar el consumo semanal de un usuario.
 */
public record WeeklyConsumptionResource(
    List<DailyConsumptionData> dailyConsumptions,
    Double totalWeeklyConsumption,
    LocalDate weekStartDate,
    LocalDate weekEndDate
) {
    /**
     * DailyConsumptionData
     * Datos de consumo por día.
     */
    public record DailyConsumptionData(
        LocalDate date,
        String dayName,
        Double consumption
    ) {}
}