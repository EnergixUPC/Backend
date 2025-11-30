package com.backendsems.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

/**
 * SignUpResource - Recurso REST para registro de usuario
 */
public record SignUpResource(
    @NotBlank String email,
    @NotBlank String password,
    @NotBlank String name,
    @NotBlank String lastName,
    String phone,
    String address
) {}