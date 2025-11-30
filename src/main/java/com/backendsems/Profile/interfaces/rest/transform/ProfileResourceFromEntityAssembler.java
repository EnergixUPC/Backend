package com.backendsems.Profile.interfaces.rest.transform;

import com.backendsems.Profile.domain.model.aggregates.Profile;
import com.backendsems.Profile.interfaces.rest.resources.ProfileResource;

/**
 * ProfileResourceFromEntityAssembler
 */
public class ProfileResourceFromEntityAssembler {
    public static ProfileResource toResourceFromEntity(Profile profile) {
        return new ProfileResource(
                profile.getId(),
                profile.getName().name(),
                profile.getLastName().name(),
                profile.getEmail().address(),
                profile.getPhone().number(),
                profile.getAddress().address()
        );
    }
}