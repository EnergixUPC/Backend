package com.backendsems.profiles.application.internal.queryservices;

import com.backendsems.profiles.domain.model.aggregates.Profile;
import com.backendsems.profiles.domain.model.queries.GetAllProfilesQuery;
import com.backendsems.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.backendsems.profiles.domain.model.queries.GetProfileByIdQuery;
import com.backendsems.profiles.domain.services.ProfileQueryService;
import com.backendsems.profiles.infrastructure.repositories.jpa.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * ProfileQueryServiceImpl
 */
@Service
public class ProfileQueryServiceImpl implements ProfileQueryService {

    private final ProfileRepository profileRepository;

    public ProfileQueryServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public List<Profile> handle(GetAllProfilesQuery query) {
        return profileRepository.findAll();
    }

    @Override
    public Optional<Profile> handle(GetProfileByEmailQuery query) {
        return profileRepository.findByEmailAddress(query.email());
    }

    @Override
    public Optional<Profile> handle(GetProfileByIdQuery query) {
        return profileRepository.findById(query.id());
    }
}