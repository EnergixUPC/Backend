package com.backendsems.iam.interfaces.rest.transform;

import com.backendsems.iam.domain.model.commands.SignInCommand;
import com.backendsems.iam.interfaces.rest.resources.SignInResource;

/**
 * SignInCommandFromResourceAssembler - Ensamblador para convertir SignInResource a SignInCommand
 */
public class SignInCommandFromResourceAssembler {

    public static SignInCommand toCommandFromResource(SignInResource resource) {
        return new SignInCommand(resource.email(), resource.password());
    }
}