package com.backendsems.profiles.domain.model.commands;

import com.backendsems.profiles.domain.model.valueobjects.Address;
import com.backendsems.profiles.domain.model.valueobjects.EmailAddress;
import com.backendsems.profiles.domain.model.valueobjects.PersonName;
import com.backendsems.profiles.domain.model.valueobjects.PhoneNumber;

/**
 * UpdateProfileCommand
 * Comando para actualizar datos de un perfil.
 */
public record UpdateProfileCommand(
        Long profileId,
        PersonName name,
        PersonName lastName,
        EmailAddress email,
        PhoneNumber phone,
        Address address,
        String profilePhotoUrl
) {
}