package com.backendsems.SEMS.application.internal.commandservices;

import com.backendsems.SEMS.domain.model.commands.CreateNotificationCommand;
import com.backendsems.SEMS.domain.model.entities.NotificationEntity;
import com.backendsems.SEMS.domain.model.events.NotificationCreatedEvent;
import com.backendsems.SEMS.domain.model.valueobjects.NotificationId;
import com.backendsems.SEMS.domain.services.NotificationCommandService;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.NotificationRepository;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * NotificationCommandServiceImpl
 * Implementación del servicio de comandos para notificaciones.
 */
@Service
public class NotificationCommandServiceImpl implements NotificationCommandService {

    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public NotificationCommandServiceImpl(NotificationRepository notificationRepository, ApplicationEventPublisher eventPublisher) {
        this.notificationRepository = notificationRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void createNotification(CreateNotificationCommand command) {
        NotificationId id = new NotificationId(UUID.randomUUID().toString());
        LocalDateTime now = LocalDateTime.now();
        NotificationEntity entity = new NotificationEntity(
                id,
                command.deviceId(),
                command.userId(),
                command.message(),
                command.type(),
                now
        );
        notificationRepository.save(entity);

        // Publicar evento
        NotificationCreatedEvent event = new NotificationCreatedEvent(id, command.deviceId(), command.userId(), command.message(), command.type(), now);
        eventPublisher.publishEvent(event);
    }
}