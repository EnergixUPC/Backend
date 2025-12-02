package com.backendsems.profiles.interfaces.rest.resources;

/**
 * UpdateProfileResource
 * Resource para actualizar perfil del usuario
 */
public record UpdateProfileResource(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String address,
        String profilePhotoUrl
) {
}