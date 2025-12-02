package com.backendsems.SEMS.application.internal.eventhandlers;

import com.backendsems.SEMS.domain.model.aggregates.Device;
import com.backendsems.SEMS.domain.model.entities.DevicePreference;
import com.backendsems.SEMS.domain.model.events.DeviceAddedEvent;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.DeviceRepository;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.PreferencesRepository;
import org.springframework.stereotype.Component;

/**
 * DeviceAddedEventHandler
 * Maneja el evento DeviceAddedEvent creando preferencias iniciales para el dispositivo.
 */
@Component
public class DeviceAddedEventHandler {

    private final PreferencesRepository preferencesRepository;
    private final DeviceRepository deviceRepository;

    public DeviceAddedEventHandler(PreferencesRepository preferencesRepository, DeviceRepository deviceRepository) {
        this.preferencesRepository = preferencesRepository;
        this.deviceRepository = deviceRepository;
    }

    /**
     * Maneja el evento DeviceAddedEvent.
     * Crea preferencias iniciales con valores por defecto.
     * @param event El evento DeviceAddedEvent.
     */
    public void handle(DeviceAddedEvent event) {
        UserId userId = event.getUserId();
        Long deviceId = event.getDeviceId();

        // Check if preferences already exist
        if (preferencesRepository.existsByUserIdAndDeviceId(userId, deviceId)) {
            return; // Already exist
        }

        // Get device
        Device device = deviceRepository.findById(deviceId).orElse(null);
        if (device == null) {
            return; // Device not found, perhaps log error
        }

        // Create default preferences
        DevicePreference preferences = new DevicePreference(userId, device, 100.0, true,
                false, false, false, false, false, false, false, false, false, false, false, false); // Default false for all booleans

        preferencesRepository.save(preferences);
    }
}