package com.backendsems.iam.domain.services;

import com.backendsems.iam.domain.model.entities.Role;
import com.backendsems.iam.domain.model.valueobjects.Roles;

import java.util.List;
import java.util.Optional;

/**
 * RoleQueryService - Servicio de dominio para consultas de roles
 */
public interface RoleQueryService {

    Optional<Role> findById(Long id);

    Optional<Role> findByName(Roles name);

    List<Role> findAll();
}