package com.backendsems.SEMS.infrastructure.repositories;

import com.backendsems.SEMS.domain.model.entities.DevicePreference;
import com.backendsems.SEMS.domain.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/**
 * Repositorio Spring Data JPA para la entidad DevicePreference.
 *
 * Proporciona CRUD heredado y consultas para obtener la preferencia
 * asociada a un usuario por la entidad `User` o por su `id`.
 */

@Repository
public interface DevicePreferenceRepository extends JpaRepository<DevicePreference, Long> {
    Optional<DevicePreference> findByUser(User user);
    Optional<DevicePreference> findByUserId(Long userId);
}