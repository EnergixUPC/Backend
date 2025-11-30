package com.backendsems.SEMS.infrastructure.persistence.jpa.repositories;

import com.backendsems.SEMS.domain.model.entities.DeviceConsumptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DeviceConsumptionRepository
 * Repositorio JPA para DeviceConsumptionEntity.
 */
@Repository
public interface DeviceConsumptionRepository extends JpaRepository<DeviceConsumptionEntity, Long> {

    /**
     * Encuentra consumos por deviceId.
     * @param deviceId El ID del dispositivo.
     * @return Lista de consumos.
     */
    List<DeviceConsumptionEntity> findByDeviceId(Long deviceId);

    /**
     * Elimina consumos por deviceId.
     * @param deviceId El ID del dispositivo.
     */
    void deleteByDeviceId(Long deviceId);
}