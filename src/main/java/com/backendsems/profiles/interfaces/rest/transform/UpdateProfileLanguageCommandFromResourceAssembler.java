package com.backendsems.profiles.interfaces.rest.transform;

import com.backendsems.profiles.domain.model.commands.UpdateProfileLanguageCommand;
import com.backendsems.profiles.interfaces.rest.resources.UpdateProfileLanguageResource;

public class UpdateProfileLanguageCommandFromResourceAssembler {
    public static UpdateProfileLanguageCommand toCommandFromResource(Long profileId, UpdateProfileLanguageResource resource) {
        return new UpdateProfileLanguageCommand(
                profileId,
                resource.language()
        );
    }
}
