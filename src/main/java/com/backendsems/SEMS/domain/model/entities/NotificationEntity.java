package com.backendsems.SEMS.domain.model.entities;

import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.model.valueobjects.NotificationId;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * NotificationEntity
 * Entidad JPA para notificaciones.
 */
@Getter
@Entity

public class NotificationEntity {

    @EmbeddedId
    private NotificationId id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "device_id"))
    private DeviceId deviceId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "user_id"))
    private UserId userId;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    protected NotificationEntity() {
        // Constructor vacío para JPA
    }

    public NotificationEntity(NotificationId id, DeviceId deviceId, UserId userId, String message, String type, LocalDateTime timestamp) {
        this.id = id;
        this.deviceId = deviceId;
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
    }
}