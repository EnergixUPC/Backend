package com.backendsems.iam.interfaces.rest.transform;

import com.backendsems.iam.domain.model.commands.SignUpCommand;
import com.backendsems.iam.interfaces.rest.resources.SignUpResource;

/**
 * SignUpCommandFromResourceAssembler - Ensamblador para convertir SignUpResource a SignUpCommand
 */
public class SignUpCommandFromResourceAssembler {

    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        return new SignUpCommand(
            resource.email(),
            resource.password(),
            resource.name(),
            resource.lastName(),
            resource.phone(),
            resource.address(),
            resource.roles()
        );
    }
}