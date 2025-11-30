package com.backendsems.SEMS.domain.model.events;

import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.model.valueobjects.NotificationId;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * NotificationCreatedEvent
 * Evento de dominio que se dispara cuando se crea una notificación.
 */
@Getter
public class NotificationCreatedEvent {
    private final NotificationId notificationId;
    private final DeviceId deviceId;
    private final UserId userId;
    private final String message;
    private final String type;
    private final LocalDateTime timestamp;

    public NotificationCreatedEvent(NotificationId notificationId, DeviceId deviceId, UserId userId, String message, String type, LocalDateTime timestamp) {
        this.notificationId = notificationId;
        this.deviceId = deviceId;
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
    }
}