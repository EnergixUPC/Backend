package com.backendsems.profiles.domain.model.commands;

public record UpdateProfileLanguageCommand(
        Long profileId,
        String language
) {
}
