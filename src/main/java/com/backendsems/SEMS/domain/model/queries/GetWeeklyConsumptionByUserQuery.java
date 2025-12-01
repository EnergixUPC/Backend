package com.backendsems.SEMS.domain.model.queries;

import com.backendsems.SEMS.domain.model.valueobjects.UserId;

/**
 * GetWeeklyConsumptionByUserQuery
 * Query para obtener el consumo semanal agregado de un usuario.
 */
public record GetWeeklyConsumptionByUserQuery(UserId userId) {
}