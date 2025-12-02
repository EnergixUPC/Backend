package com.backendsems.SEMS.interfaces.rest.resources;

import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;

/**
 * CreateNotificationResource
 * Recurso para crear notificaciones.
 */
public record CreateNotificationResource(
        DeviceId deviceId,
        UserId userId,
        String message,
        String type
) {
}