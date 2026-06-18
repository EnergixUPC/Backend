package com.backendsems.iam.interfaces.rest.resources;

public record ChangePasswordResource(String oldPassword, String newPassword) {}
