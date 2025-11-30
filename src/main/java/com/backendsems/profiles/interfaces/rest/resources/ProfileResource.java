package com.backendsems.Profile.interfaces.rest.resources;

/**
 * ProfileResource
 */
public record ProfileResource(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String address
) {
}