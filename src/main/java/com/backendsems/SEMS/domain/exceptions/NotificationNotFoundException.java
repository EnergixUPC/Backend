package com.backendsems.SEMS.domain.exceptions;

/**
 * NotificationNotFoundException
 * Excepción lanzada cuando una notificación no se encuentra.
 */
public class NotificationNotFoundException extends RuntimeException {

    public NotificationNotFoundException(String message) {
        super(message);
    }

    public NotificationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}