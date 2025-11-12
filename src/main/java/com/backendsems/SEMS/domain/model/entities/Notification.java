package com.backendsems.SEMS.domain.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Notification Entity
 * Entidad para notificaciones del sistema SEMS
 */
@Entity
@Table(name = "notification")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, name = "user_id")
    private Long userId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, length = 1000)
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
    
    @Builder.Default
    @Column(name = "is_read")
    private String isRead = "UNREAD";
    
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
    
    public enum NotificationType {
        SUCCESS, WARNING, INFO, ERROR, ENERGY_SAVING, DEVICE_ALERT, SYSTEM_UPDATE
    }
    
    // Método para marcar como leído
    public void markAsRead() {
        this.isRead = "READ";
    }
    
    // Método para verificar si está leído
    public boolean isReadStatus() {
        return "READ".equals(this.isRead);
    }
    
    // Setter para userId
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}