package com.backendsems.SEMS.application.internal.eventhandlers;

import com.backendsems.SEMS.domain.model.aggregates.Device;
import com.backendsems.SEMS.domain.model.commands.CreateNotificationCommand;
import com.backendsems.SEMS.domain.model.events.HighConsumptionDetectedEvent;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceCategory;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceLocation;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceName;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceStatus;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.domain.services.NotificationCommandService;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class HighConsumptionEventHandlerTest {

    private DeviceRepository deviceRepository;
    private NotificationCommandService notificationCommandService;
    private SimpMessagingTemplate messagingTemplate;
    private HighConsumptionEventHandler handler;

    @BeforeEach
    void setUp() {
        deviceRepository = Mockito.mock(DeviceRepository.class);
        notificationCommandService = Mockito.mock(NotificationCommandService.class);
        messagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
        handler = new HighConsumptionEventHandler(deviceRepository, notificationCommandService, messagingTemplate);
    }

    @Test
    void testHandleHighConsumptionDetectedEvent_notifiesRealDeviceOwner() {
        // Bug fix regression test: previously this handler always attributed the alert to
        // a hardcoded UserId(1L), regardless of who actually owned the device.
        Device device = new Device(new UserId(42L), new DeviceName("Sensor"), new DeviceCategory("HVAC"),
                new DeviceStatus("Online"), new DeviceLocation("Living Room"), true);
        Mockito.when(deviceRepository.findById(100L)).thenReturn(Optional.of(device));

        HighConsumptionDetectedEvent event = new HighConsumptionDetectedEvent("100", 6.5, 5.0, LocalDateTime.now());

        handler.handle(event);

        ArgumentCaptor<CreateNotificationCommand> commandCaptor = ArgumentCaptor.forClass(CreateNotificationCommand.class);
        verify(notificationCommandService).createNotification(commandCaptor.capture());

        CreateNotificationCommand command = commandCaptor.getValue();
        assertEquals(100L, command.deviceId().value());
        assertEquals(42L, command.userId().id());
        assertEquals("High consumption detected: 6.5 kW/min", command.message());
        assertEquals("ALERT", command.type());

        // US23 fix: la alerta ahora se envía solo al usuario dueño del dispositivo, no a un
        // canal global compartido por todos los clientes conectados.
        verify(messagingTemplate).convertAndSendToUser("42", "/queue/alerts", command);
    }

    @Test
    void testHandleHighConsumptionDetectedEvent_unknownDevice_doesNothing() {
        Mockito.when(deviceRepository.findById(999L)).thenReturn(Optional.empty());

        HighConsumptionDetectedEvent event = new HighConsumptionDetectedEvent("999", 6.5, 5.0, LocalDateTime.now());

        handler.handle(event);

        Mockito.verifyNoInteractions(notificationCommandService);
        Mockito.verifyNoInteractions(messagingTemplate);
    }
}
