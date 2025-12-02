package com.backendsems.SEMS.infrastructure.persistence.jpa.repositories;

import com.backendsems.SEMS.domain.model.entities.DevicePreference;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PreferencesRepository
 * Repositorio JPA para DevicePreference.
 */
@Repository
public interface PreferencesRepository extends JpaRepository<DevicePreference, Long> {

    /**
     * Encuentra preferencias por userId y deviceId.
     * @param userId El ID del usuario.
     * @param deviceId El ID del dispositivo.
     * @return Las preferencias, si existen.
     */
    Optional<DevicePreference> findByUserIdAndDeviceId(UserId userId, Long deviceId);

    /**
     * Encuentra todas las preferencias por userId.
     * @param userId El ID del usuario.
     * @return Lista de preferencias.
     */
    List<DevicePreference> findByUserId(UserId userId);

    /**
     * Verifica si existen preferencias por userId y deviceId.
     * @param userId El ID del usuario.
     * @param deviceId El ID del dispositivo.
     * @return true si existen, false otherwise.
     */
    boolean existsByUserIdAndDeviceId(UserId userId, Long deviceId);

    /**
     * Elimina preferencias por deviceId.
     * @param deviceId El ID del dispositivo.
     */
    void deleteByDeviceId(Long deviceId);
}