package com.backendsems.SEMS.interfaces.rest.resources;

import java.time.LocalDate;

/**
 * DailyConsumptionSummary
 * DTO para representar el consumo total de un usuario por día específico.
 */
public record DailyConsumptionSummary(
    LocalDate fecha,
    Double totalConsumo
) {
}