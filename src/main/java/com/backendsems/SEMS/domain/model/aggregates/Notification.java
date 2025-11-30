package com.backendsems.SEMS.domain.model.aggregates;

import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.model.valueobjects.NotificationId;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Notification Aggregate Root
 * Representa el agregado de notificación en el dominio.
 */
@Entity
public class Notification {

    @Id
    @AttributeOverride(name = "value", column = @Column(name = "notification_id"))
    private NotificationId id;
    
    @AttributeOverride(name = "value", column = @Column(name = "device_id"))
    private DeviceId deviceId;
    
    @AttributeOverride(name = "id", column = @Column(name = "user_id"))
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
