package com.backendsems.SEMS.infrastructure.persistence.jpa.repositories;

import com.backendsems.SEMS.domain.model.aggregates.UserSetting;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * SettingsRepository
 * Repositorio JPA para UserSetting.
 */
@Repository
public interface SettingsRepository extends JpaRepository<UserSetting, Long> {

    /**
     * Encuentra configuraciones por userId.
     * @param userId El ID del usuario.
     * @return Las configuraciones, si existen.
     */
    Optional<UserSetting> findByUserId(UserId userId);
}
