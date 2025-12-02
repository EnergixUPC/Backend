package com.backendsems.iam.interfaces.rest.transform;

import com.backendsems.iam.domain.model.commands.SignUpCommand;
import com.backendsems.iam.domain.model.valueobjects.Roles;
import com.backendsems.iam.interfaces.rest.resources.SignUpResource;

import java.util.ArrayList;

/**
 * SignUpCommandFromResourceAssembler - Ensamblador para convertir SignUpResource a SignUpCommand
 */
public class SignUpCommandFromResourceAssembler {

    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        // Pass role names instead of role entities - the service will handle role lookup
        var roleNames = new ArrayList<String>();
        roleNames.add(Roles.ROLE_USER.name()); // Default role for new users
        
        return new SignUpCommand(
            resource.email(),
            resource.password(),
            resource.name(),
            resource.lastName(),
            resource.phone(),
            resource.address(),
            roleNames
        );
    }
}