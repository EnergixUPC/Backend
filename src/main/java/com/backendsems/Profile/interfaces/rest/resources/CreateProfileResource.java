package com.backendsems.Profile.interfaces.rest.resources;

/**
 * CreateProfileResource
 */
public record CreateProfileResource(
        String firstName,
        String lastName,
        String email,
        String password,
        String phone,
        String address
) {
}