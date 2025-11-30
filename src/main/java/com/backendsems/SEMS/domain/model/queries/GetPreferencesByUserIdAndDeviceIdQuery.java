package com.backendsems.SEMS.domain.model.queries;

import com.backendsems.SEMS.domain.model.valueobjects.UserId;

/**
 * GetPreferencesByUserIdAndDeviceIdQuery
 * Query para obtener las preferencias de un usuario para un dispositivo específico.
 */
public record GetPreferencesByUserIdAndDeviceIdQuery(UserId userId, Long deviceId) {
}