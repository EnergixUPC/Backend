package com.backendsems.iam.application.commandhandlers;

import com.backendsems.iam.application.commands.SeedRolesCommand;
import com.backendsems.iam.domain.model.entities.Role;
import com.backendsems.iam.infrastructure.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * SeedRolesCommandHandler - Manejador para inicializar roles
 */
@Component
@RequiredArgsConstructor
public class SeedRolesCommandHandler {

    private final RoleRepository roleRepository;

    public void handle(SeedRolesCommand command) {
        // Verificar si ya existen roles
        if (roleRepository.count() > 0) {
            return; // Ya están inicializados
        }

        // Crear roles por defecto
        Role adminRole = Role.builder()
                .name("ADMIN")
                .description("Administrator role with full access")
                .build();

        Role userRole = Role.builder()
                .name("USER")
                .description("Standard user role")
                .build();

        roleRepository.save(adminRole);
        roleRepository.save(userRole);
    }
}