package com.backendsems.iam.infrastructure.persistence.jpa.repositories;

import com.backendsems.iam.domain.model.entities.Role;
import com.backendsems.iam.domain.model.valueobjects.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * RoleRepository - Repositorio JPA para roles
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByName(Roles name);

    Optional<Role> findByName(Roles name);
}