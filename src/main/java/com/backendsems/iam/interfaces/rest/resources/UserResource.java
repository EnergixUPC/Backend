package com.backendsems.iam.interfaces.rest.resources;

/**
 * UserResource - Recurso REST para representar un usuario
 */
public record UserResource(
    Long id,
    String email,
    String name,
    String lastName,
    String phone,
    String address,
    String plan
) {}