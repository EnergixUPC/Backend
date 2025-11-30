package com.backendsems.iam.domain.model.commands;

import jakarta.validation.constraints.NotBlank;

/**
 * SignInCommand - Comando para inicio de sesión
 */
public record SignInCommand(
    @NotBlank String email,
    @NotBlank String password
) {}