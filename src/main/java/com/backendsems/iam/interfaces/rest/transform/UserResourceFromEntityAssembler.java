package com.backendsems.iam.interfaces.rest.transform;

import com.backendsems.iam.domain.model.aggregates.User;
import com.backendsems.iam.interfaces.rest.resources.UserResource;

/**
 * UserResourceFromEntityAssembler - Ensamblador para convertir User a UserResource
 */
public class UserResourceFromEntityAssembler {

    public static UserResource toResourceFromEntity(User user) {
        return new UserResource(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getLastName(),
            user.getPhone(),
            user.getAddress(),
            user.getPlan()
        );
    }
}