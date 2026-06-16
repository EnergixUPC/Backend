package com.backendsems.SEMS.application.internal.eventhandlers;

import com.backendsems.SEMS.domain.model.commands.CreateNotificationCommand;
import com.backendsems.SEMS.domain.model.events.HighConsumptionDetectedEvent;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.domain.services.NotificationCommandService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class HighConsumptionEventHandler {

    private final NotificationCommandService notificationCommandService;
    private final SimpMessagingTemplate messagingTemplate;

    public HighConsumptionEventHandler(NotificationCommandService notificationCommandService, SimpMessagingTemplate messagingTemplate) {
        this.notificationCommandService = notificationCommandService;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handle(HighConsumptionDetectedEvent event) {
        CreateNotificationCommand command = new CreateNotificationCommand(
                new DeviceId(Long.parseLong(event.getDeviceId())),
                new UserId(1L),
                "High consumption detected: " + event.getConsumption() + " kW/min",
                "ALERT"
        );
        notificationCommandService.createNotification(command);
        
        messagingTemplate.convertAndSend("/topic/alerts", command);
        messagingTemplate.convertAndSend("/topic/alerts/" + event.getDeviceId(), command);
    }
}

