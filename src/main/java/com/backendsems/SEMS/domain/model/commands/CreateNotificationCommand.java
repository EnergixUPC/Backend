package com.backendsems.SEMS.domain.model.commands;

import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;

/**
 * CreateNotificationCommand
 * Comando para crear una nueva notificación.
 */
public record CreateNotificationCommand(
        DeviceId deviceId,
        UserId userId,
        String message,
        String type
) {
}