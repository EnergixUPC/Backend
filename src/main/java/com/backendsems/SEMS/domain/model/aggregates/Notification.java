package com.backendsems.SEMS.domain.model.aggregates;

import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.model.valueobjects.NotificationId;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Notification Aggregate Root
 * Representa el agregado de notificación en el dominio.
 */
public class Notification {

    private NotificationId id;
    private DeviceId deviceId;
    private UserId userId;
    private String message;
    private String type;
    private LocalDateTime timestamp;

    protected Notification() {
        // Constructor vacío para frameworks
    }

    public Notification(NotificationId id, DeviceId deviceId, UserId userId, String message, String type, LocalDateTime timestamp) {
        this.id = id;
        this.deviceId = deviceId;
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
    }

    // Factory method para crear una nueva notificación
    public static Notification create(DeviceId deviceId, UserId userId, String message, String type) {
        return new Notification(
                new NotificationId(UUID.randomUUID().toString()),
                deviceId,
                userId,
                message,
                type,
                LocalDateTime.now()
        );
    }

    // Getters
    public NotificationId getId() {
        return id;
    }

    public DeviceId getDeviceId() {
        return deviceId;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
