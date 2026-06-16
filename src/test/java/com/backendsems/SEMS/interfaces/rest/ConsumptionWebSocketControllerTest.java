package com.backendsems.SEMS.interfaces.rest;

import com.backendsems.SEMS.domain.model.events.ConsumptionRecordedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.verify;

class ConsumptionWebSocketControllerTest {

    private SimpMessagingTemplate messagingTemplate;
    private ConsumptionWebSocketController controller;

    @BeforeEach
    void setUp() {
        messagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
        controller = new ConsumptionWebSocketController(messagingTemplate);
    }

    @Test
    void testHandleConsumptionRecordedEvent() {
        ConsumptionRecordedEvent event = new ConsumptionRecordedEvent("device123", 4.5, "2023-10-01T12:00:00");
        controller.handleConsumptionRecordedEvent(event);

        verify(messagingTemplate).convertAndSend("/topic/consumptions", event);
        verify(messagingTemplate).convertAndSend("/topic/consumptions/device123", event);
    }
}

