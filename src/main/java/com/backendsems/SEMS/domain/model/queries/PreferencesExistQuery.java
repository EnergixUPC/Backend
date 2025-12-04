package com.backendsems.SEMS.domain.model.queries;

/**
 * PreferencesExistQuery
 * Query para verificar si existen preferencias para un usuario y dispositivo.
 */
public record PreferencesExistQuery(Long userId, Long deviceId) {
}