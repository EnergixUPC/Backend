package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.commands.CreateNotificationCommand;

/**
 * NotificationCommandService
 * Servicio de comandos para notificaciones.
 */
public interface NotificationCommandService {

    void createNotification(CreateNotificationCommand command);
}