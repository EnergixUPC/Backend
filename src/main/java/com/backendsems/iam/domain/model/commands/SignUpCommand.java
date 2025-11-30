package com.backendsems.iam.domain.model.commands;

import com.backendsems.iam.domain.model.entities.Role;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * SignUpCommand - Comando para registro de usuario
 */
public record SignUpCommand(
    @NotBlank String email,
    @NotBlank String password,
    @NotBlank String name,
    @NotBlank String lastName,
    String phone,
    String address,
    List<Role> roles
) {}