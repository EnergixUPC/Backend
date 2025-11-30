package com.backendsems.iam.interfaces.rest.resources;

/**
 * RoleResource - Recurso REST para representar un rol
 */
public record RoleResource(
    Long id,
    String name,
    String description
) {}