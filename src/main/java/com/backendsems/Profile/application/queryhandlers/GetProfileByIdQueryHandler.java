package com.backendsems.Profile.application.queryhandlers;

import com.backendsems.Profile.domain.model.aggregates.Profile;
import com.backendsems.Profile.domain.model.queries.GetProfileByIdQuery;
import com.backendsems.Profile.domain.model.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetProfileByIdQueryHandler {

    private final ProfileService profileService;

    public Profile handle(GetProfileByIdQuery query) {
        return profileService.getProfile(query.profileId());
    }
}