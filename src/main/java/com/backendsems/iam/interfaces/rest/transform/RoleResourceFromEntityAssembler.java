package com.backendsems.iam.interfaces.rest.transform;

import com.backendsems.iam.domain.model.entities.Role;
import com.backendsems.iam.interfaces.rest.resources.RoleResource;

/**
 * RoleResourceFromEntityAssembler - Ensamblador para convertir Role a RoleResource
 */
public class RoleResourceFromEntityAssembler {

    public static RoleResource toResourceFromEntity(Role role) {
        return new RoleResource(
            role.getId(),
            role.getStringName(),
            role.getDescription()
        );
    }
}