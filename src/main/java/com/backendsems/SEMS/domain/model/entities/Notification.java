package com.backendsems.SEMS.domain.model.entities;

import com.backendsems.SEMS.domain.model.aggregates.NotificationAggregate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Notification Entity - Compatibility layer
 * Mantiene compatibilidad con el frontend mientras usa NotificationAggregate internamente
 */
@Entity
@Table(name = "notifications_legacy")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, length = 1000)
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
    
    @Builder.Default
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    // Campos adicionales del frontend SEMS
    @Builder.Default
    @Column(name = "priority")
    private String priority = "normal";
    
    @Builder.Default
    @Column(name = "category")
    private String category = "system";
    
    @Column(name = "action_url")
    private String actionUrl;
    
    @Builder.Default
    @Column(name = "is_dismissed")
    private Boolean isDismissed = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    public enum NotificationType {
        SUCCESS, WARNING, INFO, ERROR, ENERGY_SAVING, DEVICE_ALERT, SYSTEM_UPDATE
    }
    
    // Conversion methods
    public static Notification fromAggregate(NotificationAggregate aggregate, User user) {
        NotificationType type = switch (aggregate.getType()) {
            case HIGH_CONSUMPTION -> NotificationType.WARNING;
            case DEVICE_OFFLINE -> NotificationType.ERROR;
            case ENERGY_SAVING_TIP -> NotificationType.ENERGY_SAVING;
            case SYSTEM_ALERT -> NotificationType.SYSTEM_UPDATE;
        };
        
        return Notification.builder()
                .id(aggregate.getId())
                .title(aggregate.getTitle())
                .message(aggregate.getMessage())
                .type(type)
                .timestamp(aggregate.getCreatedAt())
                .isRead(aggregate.getStatus() == NotificationAggregate.NotificationStatus.READ)
                .user(user)
                .build();
    }
    
    public NotificationAggregate toAggregate() {
        NotificationAggregate.NotificationType aggregateType = switch (this.type) {
            case WARNING -> NotificationAggregate.NotificationType.HIGH_CONSUMPTION;
            case ERROR -> NotificationAggregate.NotificationType.DEVICE_OFFLINE;
            case ENERGY_SAVING -> NotificationAggregate.NotificationType.ENERGY_SAVING_TIP;
            default -> NotificationAggregate.NotificationType.SYSTEM_ALERT;
        };
        
        NotificationAggregate.NotificationStatus status = this.isRead ? 
                NotificationAggregate.NotificationStatus.READ : 
                NotificationAggregate.NotificationStatus.UNREAD;
        
        return NotificationAggregate.builder()
                .id(this.id)
                .title(this.title)
                .message(this.message)
                .type(aggregateType)
                .status(status)
                .createdAt(this.timestamp)
                .build();
    }
}