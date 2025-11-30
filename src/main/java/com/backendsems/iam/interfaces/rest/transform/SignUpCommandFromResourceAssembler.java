package com.backendsems.iam.interfaces.rest.transform;

import com.backendsems.iam.domain.model.commands.SignUpCommand;
import com.backendsems.iam.domain.model.entities.Role;
import com.backendsems.iam.domain.model.valueobjects.Roles;
import com.backendsems.iam.interfaces.rest.resources.SignUpResource;

import java.util.List;

/**
 * SignUpCommandFromResourceAssembler - Ensamblador para convertir SignUpResource a SignUpCommand
 */
public class SignUpCommandFromResourceAssembler {

    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        List<Role> roles = List.of(new Role(Roles.ROLE_ADMIN));
        return new SignUpCommand(
            resource.email(),
            resource.password(),
            resource.name(),
            resource.lastName(),
            resource.phone(),
            resource.address(),
            roles
        );
    }
}