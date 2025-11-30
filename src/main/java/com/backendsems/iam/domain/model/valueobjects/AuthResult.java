package com.backendsems.iam.domain.model.valueobjects;

import com.backendsems.iam.domain.model.aggregates.User;

/**
 * AuthResult - Resultado de autenticación
 */
public record AuthResult(User user, String token) {
}