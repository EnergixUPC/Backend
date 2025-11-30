package com.backendsems.SEMS.domain.model.queries;

import com.backendsems.SEMS.domain.model.valueobjects.UserId;

/**
 * GetAllPreferencesByUserIdQuery
 * Query para obtener todas las preferencias de un usuario.
 */
public record GetAllPreferencesByUserIdQuery(UserId userId) {
}