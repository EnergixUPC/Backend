package com.backendsems.iam.domain.services;

import com.backendsems.iam.domain.model.commands.SeedRolesCommand;
import com.backendsems.iam.domain.model.valueobjects.Roles;

/**
 * RoleCommandService - Servicio de dominio para comandos de roles
 */
public interface RoleCommandService {

    void handle(SeedRolesCommand command);
}