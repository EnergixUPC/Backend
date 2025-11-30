package com.backendsems.Profile.infrastructure.repositories.jpa.repositories;

import com.backendsems.Profile.domain.model.aggregates.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ProfileRepository
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByEmailAddress(String email);
    boolean existsByEmailAddress(String email);
}