package com.backendsems.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

/**
 * SignInResource - Recurso REST para inicio de sesión
 */
public record SignInResource(
    @NotBlank String email,
    @NotBlank String password
) {}