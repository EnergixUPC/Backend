package com.backendsems.profiles.application.internal.commandservices;

import com.backendsems.profiles.domain.model.aggregates.Profile;
import com.backendsems.profiles.domain.model.commands.CreateProfileCommand;
import com.backendsems.profiles.domain.model.commands.UpdateProfileCommand;
import com.backendsems.profiles.domain.services.ProfileCommandService;
import com.backendsems.profiles.infrastructure.repositories.jpa.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * ProfileCommandServiceImpl
 */
@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private final ProfileRepository profileRepository;

    public ProfileCommandServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> handle(CreateProfileCommand command) {
        Profile profile = Profile.create(command);
        profileRepository.save(profile);
        return Optional.of(profile);
    }

    @Override
    public void handle(UpdateProfileCommand command) {
        Profile profile = profileRepository.findById(command.profileId()).orElseThrow(() -> new RuntimeException("Profile not found"));
        profile.update(command);
        profileRepository.save(profile);
    }
}