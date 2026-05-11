package com.backendsems.SEMS.application.internal.commandservices;

import com.backendsems.SEMS.domain.model.aggregates.Notification;
import com.backendsems.SEMS.domain.model.commands.CreateNotificationCommand;
import com.backendsems.SEMS.domain.model.events.NotificationCreatedEvent;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Notification Command Service Tests - US07, US08")
@ExtendWith(MockitoExtension.class)
public class NotificationCommandServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private NotificationCommandServiceImpl notificationCommandService;

    // ==================== US07: Generar alertas de consumo elevado ====================

    @Test
    @DisplayName("US07 - Generar alertas de consumo elevado - Escenario 1: Envío de alerta por exceso de consumo - Debe guardar notificación y publicar evento")
    void US07_createNotification_SavesNotificationAndPublishesEvent_WhenSuccessful() {

        DeviceId mockDeviceId = mock(DeviceId.class);
        UserId mockUserId = mock(UserId.class);

        CreateNotificationCommand command = new CreateNotificationCommand(
                mockDeviceId,
                mockUserId,
                "Alerta: consumo elevado detectado",
                "WARNING"
        );

        notificationCommandService.createNotification(command);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(eventPublisher, times(1)).publishEvent(any(NotificationCreatedEvent.class));
    }

    @Test
    @DisplayName("US07 - Generar alertas de consumo elevado - Escenario 2: Registro de alerta en historial - Debe registrar la alerta con datos correctos")
    void US07_createNotification_RegistersAlertInHistory_WithCorrectData() {

        DeviceId mockDeviceId = mock(DeviceId.class);
        UserId mockUserId = mock(UserId.class);

        CreateNotificationCommand command = new CreateNotificationCommand(
                mockDeviceId,
                mockUserId,
                "Historial de alertas actualizado",
                "WARNING"
        );

        notificationCommandService.createNotification(command);

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification savedNotification = notificationCaptor.getValue();
        assertNotNull(savedNotification);
        assertEquals("Historial de alertas actualizado", savedNotification.getMessage());
        assertEquals("WARNING", savedNotification.getType());
    }

    // ==================== US08: Configurar umbrales de alerta personalizados ====================

    @Test
    @DisplayName("US08 - Configurar umbrales de alerta personalizados - Escenario 1: Generación de alerta personalizada - Debe generar alerta con mensaje personalizado")
    void US08_createNotification_GeneratesCustomAlert_WhenThresholdExceeded() {

        DeviceId mockDeviceId = mock(DeviceId.class);
        UserId mockUserId = mock(UserId.class);

        CreateNotificationCommand command = new CreateNotificationCommand(
                mockDeviceId,
                mockUserId,
                "Tu dispositivo ha superado el límite configurado",
                "CUSTOM_THRESHOLD"
        );

        notificationCommandService.createNotification(command);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(eventPublisher, times(1)).publishEvent(any(NotificationCreatedEvent.class));
    }
}