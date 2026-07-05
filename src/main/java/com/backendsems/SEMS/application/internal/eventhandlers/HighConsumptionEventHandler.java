package com.backendsems.SEMS.application.internal.eventhandlers;

import com.backendsems.SEMS.domain.model.commands.CreateNotificationCommand;
import com.backendsems.SEMS.domain.model.events.HighConsumptionDetectedEvent;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.services.NotificationCommandService;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.DeviceRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Complementa a HighConsumptionDetectedEventHandler: transmite la alerta en tiempo real por
 * WebSocket, dirigida únicamente al usuario dueño del dispositivo (ver {@link
 * com.backendsems.SEMS.infrastructure.config.StompAuthChannelInterceptor}).
 */
@Component
public class HighConsumptionEventHandler {

    private final DeviceRepository deviceRepository;
    private final NotificationCommandService notificationCommandService;
    private final SimpMessagingTemplate messagingTemplate;

    public HighConsumptionEventHandler(DeviceRepository deviceRepository,
                                       NotificationCommandService notificationCommandService,
                                       SimpMessagingTemplate messagingTemplate) {
        this.deviceRepository = deviceRepository;
        this.notificationCommandService = notificationCommandService;
        this.messagingTemplate = messagingTemplate;
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

        CreateNotificationCommand command = new CreateNotificationCommand(
                new DeviceId(deviceIdLong),
                device.getUserId(),
                "High consumption detected: " + event.getConsumption() + " kW/min",
                "ALERT"
        );
        notificationCommandService.createNotification(command);

        messagingTemplate.convertAndSendToUser(device.getUserId().id().toString(), "/queue/alerts", command);
    }
}

