package com.backendsems.iam.application.internal.queryservice;

import com.backendsems.iam.domain.model.entities.Role;
import com.backendsems.iam.domain.model.valueobjects.Roles;
import com.backendsems.iam.domain.services.RoleQueryService;
import com.backendsems.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * RoleQueryServiceImpl - Implementación del servicio de consultas para roles
 */
@Service
public class RoleQueryServiceImpl implements RoleQueryService {

    private final RoleRepository roleRepository;

    public RoleQueryServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Optional<Role> findByName(Roles name) {
        return roleRepository.findByName(name);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}