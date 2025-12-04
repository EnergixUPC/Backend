package com.backendsems.SEMS.application.internal.eventhandlers;

import com.backendsems.SEMS.domain.model.entities.DevicePreference;
import com.backendsems.SEMS.domain.model.events.DeviceAddedEvent;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.PreferencesRepository;
import org.springframework.stereotype.Component;

/**
 * DeviceAddedEventHandler
 * Maneja el evento DeviceAddedEvent creando preferencias iniciales para el dispositivo.
 */
@Component
public class DeviceAddedEventHandler {

    private final PreferencesRepository preferencesRepository;

    public DeviceAddedEventHandler(PreferencesRepository preferencesRepository) {
        this.preferencesRepository = preferencesRepository;
    }

    /**
     * Maneja el evento DeviceAddedEvent.
     * Crea preferencias iniciales con valores por defecto si no existen.
     * @param event El evento DeviceAddedEvent.
     */
    public void handle(DeviceAddedEvent event) {
        Long userId = event.getUserId().id();

        // Check if preferences already exist for this user
        if (preferencesRepository.existsByUserId(userId)) {
            return; // Already exist
        }

        // Create default preferences for the user
        DevicePreference preferences = new DevicePreference(userId, 100.0, true,
                false, false, false, false, false, false, false, false, false, false, false, false); // Default false for all booleans

        preferencesRepository.save(preferences);
    }
}