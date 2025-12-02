package com.backendsems.profiles.interfaces.rest.transform;

import com.backendsems.profiles.domain.model.commands.UpdateProfileCommand;
import com.backendsems.profiles.domain.model.valueobjects.Address;
import com.backendsems.profiles.domain.model.valueobjects.EmailAddress;
import com.backendsems.profiles.domain.model.valueobjects.PersonName;
import com.backendsems.profiles.domain.model.valueobjects.PhoneNumber;
import com.backendsems.profiles.interfaces.rest.resources.UpdateProfileResource;

/**
 * Assembler to convert an UpdateProfileResource to an UpdateProfileCommand.
 */
public class UpdateProfileCommandFromResourceAssembler {
    /**
     * Converts an UpdateProfileResource to an UpdateProfileCommand.
     * @param resource The {@link UpdateProfileResource} resource to convert.
     * @return The {@link UpdateProfileCommand} command.
     */
    public static UpdateProfileCommand toCommandFromResource(UpdateProfileResource resource) {
        return new UpdateProfileCommand(
                resource.id(),
                new PersonName(resource.firstName()),
                new PersonName(resource.lastName()),
                new EmailAddress(resource.email()),
                new PhoneNumber(resource.phone()),
                new Address(resource.address()),
                resource.profilePhotoUrl()
        );
    }
}