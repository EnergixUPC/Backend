package com.backendsems.profiles.interfaces.rest.resources;

/**
 * UpdateProfileResource
 */
public record UpdateProfileResource(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String address
) {
}