package com.backendsems.SEMS.infrastructure.persistence.jpa.repositories;

import com.backendsems.SEMS.domain.model.entities.DevicePreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * PreferencesRepository
 * Repositorio JPA para DevicePreference.
 */
@Repository
public interface PreferencesRepository extends JpaRepository<DevicePreference, Long> {

    /**
     * Encuentra preferencias por userId.
     * @param userId El ID del usuario.
     * @return Las preferencias del usuario, si existen.
     */
    Optional<DevicePreference> findByUserId(Long userId);

    /**
     * Verifica si existen preferencias por userId.
     * @param userId El ID del usuario.
     * @return true si existen, false otherwise.
     */
    boolean existsByUserId(Long userId);
}