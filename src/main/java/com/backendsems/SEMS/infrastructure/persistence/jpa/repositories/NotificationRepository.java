package com.backendsems.SEMS.infrastructure.persistence.jpa.repositories;

import com.backendsems.SEMS.domain.model.aggregates.Notification;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.model.valueobjects.NotificationId;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * NotificationRepository
 * Repositorio JPA para el aggregate Notification.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, NotificationId> {

    List<Notification> findByDeviceId(DeviceId deviceId);

    List<Notification> findByUserId(UserId userId);
}