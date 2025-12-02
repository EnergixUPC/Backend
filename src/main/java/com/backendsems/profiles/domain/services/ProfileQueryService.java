package com.backendsems.profiles.domain.services;

import com.backendsems.profiles.domain.model.aggregates.Profile;
import com.backendsems.profiles.domain.model.queries.GetAllProfilesQuery;
import com.backendsems.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.backendsems.profiles.domain.model.queries.GetProfileByIdQuery;

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