package com.backendsems.SEMS.domain.exceptions;

import com.backendsems.SEMS.domain.model.valueobjects.UserId;

/**
 * PreferencesNotFoundException
 * Excepción lanzada cuando las preferencias no se encuentran.
 */
public class PreferencesNotFoundException extends RuntimeException {
    public PreferencesNotFoundException(UserId userId, Long deviceId) {
        super("Preferences for user " + userId + " and device " + deviceId + " not found");
    }
}