package com.backendsems.profiles.interfaces.rest.resources;

/**
 * ProfileResource
 * Resource para exponer datos del perfil (sin password por seguridad)
 */
public record ProfileResource(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String address,
        String profilePhotoUrl
) {
}