package com.backendsems.profiles.application.internal.commandservices;

import com.backendsems.iam.interfaces.acl.IamContextFacade;
import com.backendsems.profiles.domain.model.aggregates.Profile;
import com.backendsems.profiles.domain.model.commands.CreateProfileCommand;
import com.backendsems.profiles.domain.model.commands.UpdateProfileCommand;
import com.backendsems.profiles.domain.services.ProfileCommandService;
import com.backendsems.profiles.infrastructure.repositories.jpa.repositories.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * ProfileCommandServiceImpl
 */
@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {
    private static final Logger logger = LoggerFactory.getLogger(ProfileCommandServiceImpl.class);
    private final ProfileRepository profileRepository;
    private final IamContextFacade iamContextFacade;

    public ProfileCommandServiceImpl(ProfileRepository profileRepository, IamContextFacade iamContextFacade) {
        this.profileRepository = profileRepository;
        this.iamContextFacade = iamContextFacade;
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
        
        // Obtener el email anterior
        String oldEmail = profile.getEmail().address();
        
        // Actualizar el perfil
        profile.update(command);
        profileRepository.save(profile);
        
        // Si el email cambió, sincronizar con la tabla users
        String newEmail = command.email().address();
        if (!oldEmail.equals(newEmail)) {
            logger.info("Email changed from {} to {}, syncing with users table", oldEmail, newEmail);
            boolean success = iamContextFacade.updateUserEmail(command.profileId(), newEmail);
            if (!success) {
                logger.warn("Failed to sync email with users table for profile ID {}", command.profileId());
            }
        }
    }
}