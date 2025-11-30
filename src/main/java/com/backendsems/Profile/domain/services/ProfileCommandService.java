package com.backendsems.Profile.domain.services;

import com.backendsems.Profile.domain.model.aggregates.Profile;
import com.backendsems.Profile.domain.model.commands.CreateProfileCommand;

import java.util.Optional;

/**
 * ProfileCommandService
 */
public interface ProfileCommandService {
    Optional<Profile> handle(CreateProfileCommand command);
}