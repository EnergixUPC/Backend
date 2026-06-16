package com.backendsems.profiles.domain.services;

import com.backendsems.profiles.domain.model.aggregates.Profile;
import com.backendsems.profiles.domain.model.commands.CreateProfileCommand;
import com.backendsems.profiles.domain.model.commands.UpdateProfileCommand;

import com.backendsems.profiles.domain.model.commands.UpdateProfileLanguageCommand;

import java.util.Optional;

/**
 * ProfileCommandService
 */
public interface ProfileCommandService {
    Optional<Profile> handle(CreateProfileCommand command);
    void handle(UpdateProfileCommand command);
    void handle(UpdateProfileLanguageCommand command);
}