package com.backendsems.Profile.application.commandhandlers;

import com.backendsems.Profile.domain.model.aggregates.Profile;
import com.backendsems.Profile.domain.model.commands.UpdateProfileCommand;
import com.backendsems.Profile.domain.model.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateProfileCommandHandler {

    private final ProfileService profileService;

    public Profile handle(UpdateProfileCommand command) {
        return profileService.updateProfile(command);
    }
}