package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.aggregates.Notification;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;

import java.util.List;

/**
 * NotificationQueryService
 * Servicio de queries para notificaciones.
 */
public interface NotificationQueryService {

    List<Notification> getNotificationsByDeviceId(DeviceId deviceId);

    List<Notification> getNotificationsByUserId(UserId userId);

    List<Notification> getAllNotifications();
}