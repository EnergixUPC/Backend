package com.backendsems.SEMS.interfaces.rest.resources;

import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.model.valueobjects.NotificationId;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;

import java.time.LocalDateTime;

/**
 * NotificationResource
 * Recurso REST para notificaciones.
 */
public record NotificationResource(
        NotificationId notificationId,
        DeviceId deviceId,
        UserId userId,
        String message,
        String type,
        LocalDateTime timestamp
) {
}