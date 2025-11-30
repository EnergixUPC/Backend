package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.entities.NotificationEntity;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;

import java.util.List;

/**
 * NotificationQueryService
 * Servicio de queries para notificaciones.
 */
public interface NotificationQueryService {

    List<NotificationEntity> getNotificationsByDeviceId(DeviceId deviceId);

    List<NotificationEntity> getNotificationsByUserId(UserId userId);

    List<NotificationEntity> getAllNotifications();
}