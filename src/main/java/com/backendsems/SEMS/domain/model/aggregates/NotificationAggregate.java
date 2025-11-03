package com.backendsems.SEMS.domain.model.aggregates;

import com.backendsems.SEMS.domain.model.valueobjects.NotificationId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Notification Aggregate Root
 * Gestiona las notificaciones del sistema
 */
@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class NotificationAggregate {
    
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
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAggregate user;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime readAt;
    
    public enum NotificationType {
        HIGH_CONSUMPTION, DEVICE_OFFLINE, ENERGY_SAVING_TIP, SYSTEM_ALERT
    }
    
    public enum NotificationStatus {
        UNREAD, READ, DISMISSED
    }
    
    // Domain Methods
    public void markAsRead() {
        this.status = NotificationStatus.READ;
        this.readAt = LocalDateTime.now();
    }
    
    public void dismiss() {
        this.status = NotificationStatus.DISMISSED;
        this.readAt = LocalDateTime.now();
    }
    
    public boolean isUnread() {
        return status == NotificationStatus.UNREAD;
    }
    
    public NotificationId getNotificationId() {
        return new NotificationId(this.id);
    }
    
    public static NotificationAggregate createHighConsumptionAlert(
            UserAggregate user, 
            String deviceName, 
            Double consumption) {
        return NotificationAggregate.builder()
                .title("Alto Consumo Detectado")
                .message(String.format("El dispositivo %s ha registrado un consumo alto de %.2f kWh", 
                        deviceName, consumption))
                .type(NotificationType.HIGH_CONSUMPTION)
                .status(NotificationStatus.UNREAD)
                .user(user)
                .build();
    }
}