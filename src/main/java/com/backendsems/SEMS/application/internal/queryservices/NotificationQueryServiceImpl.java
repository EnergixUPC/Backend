package com.backendsems.SEMS.application.internal.queryservices;

import com.backendsems.SEMS.domain.model.aggregates.Notification;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.domain.services.NotificationQueryService;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.NotificationRepository;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * NotificationQueryServiceImpl
 * Implementación del servicio de queries para notificaciones.
 */
@Service
public class NotificationQueryServiceImpl implements NotificationQueryService {

    private final NotificationRepository notificationRepository;

    public NotificationQueryServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<Notification> getNotificationsByDeviceId(DeviceId deviceId) {
        return notificationRepository.findByDeviceId(deviceId);
    }

    @Override
    public List<Notification> getNotificationsByUserId(UserId userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
}