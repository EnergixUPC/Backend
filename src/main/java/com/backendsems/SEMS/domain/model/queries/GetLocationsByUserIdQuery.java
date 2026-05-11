package com.backendsems.SEMS.domain.model.queries;

import com.backendsems.SEMS.domain.model.valueobjects.UserId;

/**
 * GetLocationsByUserIdQuery
 * Query para obtener ubicaciones por usuario.
 */
public record GetLocationsByUserIdQuery(UserId userId) {}
