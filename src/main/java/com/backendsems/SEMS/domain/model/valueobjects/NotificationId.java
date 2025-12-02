package com.backendsems.SEMS.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * NotificationId
 * Value Object para el ID de una notificación.
 */
@Embeddable
public record NotificationId(String value) {
    public NotificationId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Notification ID cannot be null or blank");
        }
    }
}