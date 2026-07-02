package com.backendsems.SEMS.application.internal.eventhandlers;

import com.backendsems.SEMS.domain.model.commands.CreateNotificationCommand;
import com.backendsems.SEMS.domain.model.events.PeakHourHighConsumptionDetectedEvent;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.domain.services.NotificationCommandService;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.DeviceRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * US23: escucha PeakHourHighConsumptionDetectedEvent y crea una notificación distinta a la de
 * alto consumo genérico, indicando explícitamente que el consumo ocurrió en horario punta.
 */
@Component
public class PeakHourHighConsumptionEventHandler {

    private final DeviceRepository deviceRepository;
    private final NotificationCommandService notificationCommandService;
    private final SimpMessagingTemplate messagingTemplate;

    public PeakHourHighConsumptionEventHandler(DeviceRepository deviceRepository,
                                               NotificationCommandService notificationCommandService,
                                               SimpMessagingTemplate messagingTemplate) {
        this.deviceRepository = deviceRepository;
        this.notificationCommandService = notificationCommandService;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handle(PeakHourHighConsumptionDetectedEvent event) {
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
            "Estás consumiendo en horario pico, considera posponer el uso de '%s' (%.2f kW/min).",
            deviceName, event.getConsumption()
        );

        CreateNotificationCommand command = new CreateNotificationCommand(
                new DeviceId(deviceIdLong),
                new UserId(event.getUserId()),
                message,
                "PEAK_HOUR_HIGH_CONSUMPTION"
        );
        notificationCommandService.createNotification(command);

        messagingTemplate.convertAndSend("/topic/alerts", command);
        messagingTemplate.convertAndSend("/topic/alerts/" + event.getDeviceId(), command);
    }
}
