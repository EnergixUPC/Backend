package com.backendsems.SEMS.infrastructure.persistence.jpa.repositories;

import com.backendsems.SEMS.domain.model.entities.NotificationEntity;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.model.valueobjects.NotificationId;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * NotificationRepository
 * Repositorio JPA para NotificationEntity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, NotificationId> {

    List<NotificationEntity> findByDeviceId(DeviceId deviceId);

    List<NotificationEntity> findByUserId(UserId userId);
}