package com.backendsems.iam.interfaces.rest.resources;

import com.backendsems.iam.domain.model.entities.Role;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * SignUpResource - Recurso REST para registro de usuario
 */
public record SignUpResource(
    @NotBlank String email,
    @NotBlank String password,
    @NotBlank String name,
    @NotBlank String lastName,
    String phone,
    String address,
    List<Role> roles
) {}