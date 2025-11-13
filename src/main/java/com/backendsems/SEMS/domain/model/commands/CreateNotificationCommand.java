package com.backendsems.SEMS.domain.model.commands;

import com.backendsems.SEMS.domain.model.aggregates.NotificationAggregate;

/**
 * Command para crear una nueva Notification Aggregate
 */
public record CreateNotificationCommand(
        String title,
        String message,
        NotificationAggregate.NotificationType type,
        Long userId
) {
    /**
     * Constructor que incluye validaciones
     */
    public CreateNotificationCommand {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Título de la notificación es requerido");
        }
        
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Mensaje de la notificación es requerido");
        }
        
        if (type == null) {
            throw new IllegalArgumentException("Tipo de notificación es requerido");
        }
        
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID de usuario válido es requerido");
        }
    }
}