package com.backendsems.profiles.application.acl;

import com.backendsems.profiles.domain.model.aggregates.Profile;
import com.backendsems.profiles.domain.model.commands.CreateProfileCommand;
import com.backendsems.profiles.domain.model.commands.UpdateProfileCommand;
import com.backendsems.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.backendsems.profiles.domain.model.queries.GetProfileByIdQuery;
import com.backendsems.profiles.domain.services.ProfileCommandService;
import com.backendsems.profiles.domain.services.ProfileQueryService;
import org.springframework.stereotype.Service;

/**
 * ProfilesContextFacade
 * Fachada para exponer servicios de profiles a otros bounded contexts.
 */
@Service
public class ProfilesContextFacade implements com.backendsems.profiles.interfaces.acl.ProfilesContextFacade {

    private final ProfileCommandService commandService;
    private final ProfileQueryService queryService;

    public ProfilesContextFacade(ProfileCommandService commandService, ProfileQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    public Profile getProfileById(Long profileId) {
        GetProfileByIdQuery query = new GetProfileByIdQuery(profileId);
        return queryService.handle(query).orElse(null);
    }

    public void updateProfile(UpdateProfileCommand command) {
        commandService.handle(command);
    }

    @Override
    public Long createProfile(String firstName, String lastName, String email, String password, String phone, String address) {
        CreateProfileCommand command = new CreateProfileCommand(firstName, lastName, email, password, phone, address);
        Profile profile = commandService.handle(command).orElse(null);
        return profile != null ? profile.getId() : null;
    }

    @Override
    public Long fetchProfileIdByEmail(String email) {
        GetProfileByEmailQuery query = new GetProfileByEmailQuery(email);
        Profile profile = queryService.handle(query).orElse(null);
        return profile != null ? profile.getId() : null;
    }
}