package com.backendsems.iam.interfaces.rest.resources;

public record TokenResponseResource(String accessToken, String refreshToken, int expiresIn, String tokenType) {}
