package com.backendsems.SEMS.domain.model.queries;

import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;

/**
 * GetNotificationsByDeviceIdQuery
 * Query para obtener notificaciones por deviceId.
 */
public record GetNotificationsByDeviceIdQuery(DeviceId deviceId) {
}