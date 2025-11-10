// src/main/java/com/backendsems/SEMS/infrastructure/repositories/UserSettingsRepository.java
package com.backendsems.SEMS.infrastructure.repositories;

import com.backendsems.SEMS.domain.model.entities.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    Optional<UserSettings> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}