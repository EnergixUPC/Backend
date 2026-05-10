package com.backendsems.SEMS.application.internal.eventhandlers;

import com.backendsems.SEMS.domain.model.commands.CreateNotificationCommand;
import com.backendsems.SEMS.domain.model.events.HighConsumptionDetectedEvent;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.services.NotificationCommandService;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.DeviceRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Escucha HighConsumptionDetectedEvent y crea una notificación indicando
 * qué dispositivo superó el umbral de 5 kW/min.
 */
@Component
public class HighConsumptionDetectedEventHandler {

    private final DeviceRepository deviceRepository;
    private final NotificationCommandService notificationCommandService;

    public HighConsumptionDetectedEventHandler(DeviceRepository deviceRepository,
                                               NotificationCommandService notificationCommandService) {
        this.deviceRepository = deviceRepository;
        this.notificationCommandService = notificationCommandService;
    }

    @EventListener
    public void handle(HighConsumptionDetectedEvent event) {
        Long deviceIdLong;
        try {
            deviceIdLong = Long.parseLong(event.getDeviceId());
        } catch (NumberFormatException e) {
            return;
        }

        var device = deviceRepository.findById(deviceIdLong).orElse(null);
        if (device == null) return;

        String deviceName = device.getName().name();
        String message = String.format(
            "El dispositivo '%s' ha superado el límite de consumo con %.2f kW/min (umbral: 5 kW/min).",
            deviceName, event.getConsumption()
        );

        notificationCommandService.createNotification(new CreateNotificationCommand(
                new DeviceId(deviceIdLong),
                device.getUserId(),
                message,
                "HIGH_CONSUMPTION"
        ));
    }
}