package com.backendsems.SEMS.application.internal.eventhandlers;

import com.backendsems.SEMS.domain.model.commands.CreateNotificationCommand;
import com.backendsems.SEMS.domain.model.events.HighConsumptionDetectedEvent;
import com.backendsems.SEMS.domain.services.NotificationCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class HighConsumptionEventHandlerTest {

    private NotificationCommandService notificationCommandService;
    private SimpMessagingTemplate messagingTemplate;
    private HighConsumptionEventHandler handler;

    @BeforeEach
    void setUp() {
        notificationCommandService = Mockito.mock(NotificationCommandService.class);
        messagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
        handler = new HighConsumptionEventHandler(notificationCommandService, messagingTemplate);
    }

    @Test
    void testHandleHighConsumptionDetectedEvent() {
        HighConsumptionDetectedEvent event = new HighConsumptionDetectedEvent("100", 6.5);
        
        handler.handle(event);

        ArgumentCaptor<CreateNotificationCommand> commandCaptor = ArgumentCaptor.forClass(CreateNotificationCommand.class);
        verify(notificationCommandService).createNotification(commandCaptor.capture());
        
        CreateNotificationCommand command = commandCaptor.getValue();
        assertEquals(100L, command.deviceId().value());
        assertEquals(1L, command.userId().id());
        assertEquals("High consumption detected: 6.5 kW/min", command.message());
        assertEquals("ALERT", command.type());

        verify(messagingTemplate).convertAndSend("/topic/alerts", command);
        verify(messagingTemplate).convertAndSend("/topic/alerts/100", command);
    }
}

