package com.backendsems.SEMS.domain.model.queries;

/**
 * GetPreferencesByUserIdAndDeviceIdQuery
 * Query para obtener las preferencias de un usuario para un dispositivo específico.
 */
public record GetPreferencesByUserIdAndDeviceIdQuery(Long userId, Long deviceId) {
}