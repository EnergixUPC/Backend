package com.backendsems.profiles.application.acl;

import com.backendsems.profiles.domain.model.aggregates.Profile;
import com.backendsems.profiles.domain.model.commands.UpdateProfileCommand;
import com.backendsems.profiles.domain.model.queries.GetProfileByIdQuery;
import com.backendsems.profiles.domain.services.ProfileCommandService;
import com.backendsems.profiles.domain.services.ProfileQueryService;

/**
 * ProfilesContextFacade
 * Fachada para exponer servicios de profiles a otros bounded contexts.
 */
public class ProfilesContextFacade {

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

    // Otros métodos según necesidad
}