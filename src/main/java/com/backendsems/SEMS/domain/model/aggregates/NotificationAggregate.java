package com.backendsems.SEMS.domain.model.aggregates;

import com.backendsems.SEMS.domain.model.commands.CreateNotificationCommand;
import com.backendsems.shared.infrastructure.persistence.jpa.AuditableAbstractAggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Notification Aggregate Root
 * Representa una notificación en el sistema SEMS siguiendo principios DDD
 * Extiende AuditableAbstractAggregateRoot para capacidades de auditoría
 */
// @Entity  // TEMPORALMENTE DESHABILITADO PARA EVITAR DUPLICACIÓN DE TABLAS
@Getter
public class NotificationAggregate extends AuditableAbstractAggregateRoot<NotificationAggregate> {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    private LocalDateTime readAt;

    public enum NotificationType {
        HIGH_CONSUMPTION_ALERT, DEVICE_OFFLINE, DEVICE_MAINTENANCE, SYSTEM_UPDATE, ENERGY_SAVINGS_TIP
    }

    public enum NotificationStatus {
        SENT, READ, DISMISSED
    }

    /**
     * Constructor por defecto requerido por JPA
     */
    public NotificationAggregate() {
        super();
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public NotificationAggregate(CreateNotificationCommand command) {
        this();
        this.title = command.title();
        this.message = command.message();
        this.type = command.type();
        this.userId = command.userId();
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public NotificationAggregate(String title, String message, NotificationType type, Long userId) {
        this();
        this.title = title;
        this.message = message;
        this.type = type;
        this.userId = userId;
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    // Domain Methods
    public void markAsRead() {
        if (this.status == NotificationStatus.SENT) {
            this.status = NotificationStatus.READ;
            this.readAt = LocalDateTime.now();
        }
    }

    public void dismiss() {
        this.status = NotificationStatus.DISMISSED;
        if (this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }

    public boolean isRead() {
        return this.status == NotificationStatus.READ || this.status == NotificationStatus.DISMISSED;
    }

    public boolean isPending() {
        return this.status == NotificationStatus.SENT;
    }

    public boolean isHighPriority() {
        return this.type == NotificationType.HIGH_CONSUMPTION_ALERT || 
               this.type == NotificationType.DEVICE_OFFLINE;
    }

    public boolean belongsToUser(Long userId) {
        return this.userId.equals(userId);
    }

    public String getNotificationTitle() {
        return this.title;
    }

    public String getNotificationMessage() {
        return this.message;
    }

    public long getMinutesSinceSent() {
        return java.time.Duration.between(this.sentAt, LocalDateTime.now()).toMinutes();
    }
}