package com.backendsems.iam.infrastructure.persistence.jpa.repositories;

import com.backendsems.iam.domain.model.entities.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {
    Optional<RevokedToken> findByToken(String token);
    boolean existsByToken(String token);
}
