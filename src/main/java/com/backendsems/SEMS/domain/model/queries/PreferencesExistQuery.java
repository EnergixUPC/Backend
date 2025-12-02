package com.backendsems.SEMS.domain.model.queries;

import com.backendsems.SEMS.domain.model.valueobjects.UserId;

/**
 * PreferencesExistQuery
 * Query para verificar si existen preferencias para un usuario y dispositivo.
 */
public record PreferencesExistQuery(UserId userId, Long deviceId) {
}