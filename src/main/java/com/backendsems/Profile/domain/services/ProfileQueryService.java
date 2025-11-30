package com.backendsems.Profile.domain.services;

import com.backendsems.Profile.domain.model.aggregates.Profile;
import com.backendsems.Profile.domain.model.queries.GetAllProfilesQuery;
import com.backendsems.Profile.domain.model.queries.GetProfileByEmailQuery;
import com.backendsems.Profile.domain.model.queries.GetProfileByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * ProfileQueryService
 */
public interface ProfileQueryService {
    List<Profile> handle(GetAllProfilesQuery query);
    Optional<Profile> handle(GetProfileByEmailQuery query);
    Optional<Profile> handle(GetProfileByIdQuery query);
}