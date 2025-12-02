package com.backendsems.SEMS.infrastructure.persistence.jpa.repositories;

import com.backendsems.SEMS.domain.model.aggregates.Device;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DeviceRepository
 * Repositorio JPA para el aggregate Device.
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    /**
     * Encuentra dispositivos por userId.
     * @param userId El ID del usuario.
     * @return Lista de dispositivos.
     */
    List<Device> findByUserId(UserId userId);

    /**
     * Verifica si existe un dispositivo por ID.
     * @param deviceId El ID del dispositivo.
     * @return true si existe, false otherwise.
     */
    boolean existsById(Long deviceId);
}