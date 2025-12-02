package com.backendsems.iam.interfaces.rest.transform;

import com.backendsems.iam.domain.model.aggregates.User;
import com.backendsems.iam.interfaces.rest.resources.AuthenticatedUserResource;

/**
 * AuthenticatedUserResourceFromEntityAssembler
 */
public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user);
        return new AuthenticatedUserResource(userResource, token);
    }
}