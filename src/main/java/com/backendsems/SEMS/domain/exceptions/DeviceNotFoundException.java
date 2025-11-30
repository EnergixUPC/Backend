package com.backendsems.SEMS.domain.exceptions;

/**
 * DeviceNotFoundException
 * Excepción lanzada cuando un dispositivo no se encuentra.
 */
public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(Long deviceId) {
        super("Device with id " + deviceId + " not found");
    }
}