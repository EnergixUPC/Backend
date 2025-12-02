package com.backendsems.iam.interfaces.rest.resources;

import com.backendsems.iam.interfaces.rest.resources.UserResource;

/**
 * AuthenticatedUserResource
 */
public record AuthenticatedUserResource(UserResource user, String token) {
}