package com.backendsems.SEMS.domain.model.aggregates;

import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.model.valueobjects.NotificationId;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Notification Aggregate Root
 * Representa el agregado de notificación en el dominio.
 */
@Getter
@Entity
public class Notification {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "notification_id"))
    private NotificationId id;
    
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "device_id"))
    private DeviceId deviceId;
    
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id"))
    private UserId userId;
    
    @Column(nullable = false)
    private String message;
    
    @Column(nullable = false)
    private String type;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;

    protected Notification() {
        // Constructor vacío para JPA
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
}
