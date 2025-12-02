package com.backendsems.profiles.interfaces.rest.transform;

import com.backendsems.profiles.domain.model.aggregates.Profile;
import com.backendsems.profiles.interfaces.rest.resources.ProfileResource;

/**
 * ProfileResourceFromEntityAssembler
 * Convierte Profile a ProfileResource (sin exponer password)
 */
public class ProfileResourceFromEntityAssembler {
    public static ProfileResource toResourceFromEntity(Profile profile) {
        return new ProfileResource(
                profile.getId(),
                profile.getName().name(),
                profile.getLastName().name(),
                profile.getEmail().address(),
                profile.getPhone().number(),
                profile.getAddress().address(),
                profile.getProfilePhotoUrl()
        );
    }
}