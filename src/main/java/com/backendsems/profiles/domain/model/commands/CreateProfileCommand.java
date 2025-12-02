package com.backendsems.profiles.domain.model.commands;

/**
 * CreateProfileCommand
 */
public record CreateProfileCommand(
        String name,
        String lastName,
        String email,
        String password,
        String phone,
        String address
) {
}