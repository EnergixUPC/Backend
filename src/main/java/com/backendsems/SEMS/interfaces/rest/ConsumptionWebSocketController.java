package com.backendsems.SEMS.interfaces.rest;

import com.backendsems.SEMS.domain.model.events.ConsumptionRecordedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ConsumptionWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public ConsumptionWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleConsumptionRecordedEvent(ConsumptionRecordedEvent event) {
        messagingTemplate.convertAndSend("/topic/consumptions", event);
        messagingTemplate.convertAndSend("/topic/consumptions/" + event.getDeviceId(), event);
    }
}

