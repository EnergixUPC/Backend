package com.backendsems.Profile.domain.model.services;

import com.backendsems.Profile.domain.model.aggregates.Profile;
import com.backendsems.Profile.domain.model.commands.UpdateProfileCommand;
import com.backendsems.Profile.domain.model.valueobjects.EmailAddress;
import com.backendsems.Profile.domain.model.valueobjects.PersonName;
import com.backendsems.Profile.domain.model.valueobjects.StreetAddress;
import com.backendsems.Profile.infrastructure.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * ProfileService - Servicio de dominio para gestión de perfiles
 */
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public Profile getProfile(Long userId) {
        return profileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    public Profile updateProfile(UpdateProfileCommand command) {
        Profile profile = getProfile(command.getUserId());

        // Actualizar campos
        profile.setPersonName(new PersonName(command.getFirstName(), command.getLastName()));
        profile.setEmailAddress(new EmailAddress(command.getEmail()));
        profile.setStreetAddress(new StreetAddress(command.getAddress()));
        profile.setPhoneNumber(command.getPhoneNumber());
        profile.setProfilePhotoUrl(command.getProfilePhotoUrl());

        return profileRepository.save(profile);
    }

    public Profile createProfile(Long userId, String firstName, String lastName, String email, String address, String phoneNumber) {
        Profile profile = Profile.builder()
            .userId(userId)
            .personName(new PersonName(firstName, lastName))
            .emailAddress(new EmailAddress(email))
            .streetAddress(new StreetAddress(address))
            .phoneNumber(phoneNumber)
            .build();

        return profileRepository.save(profile);
    }
}