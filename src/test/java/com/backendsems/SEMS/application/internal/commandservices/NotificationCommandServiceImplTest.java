package com.backendsems.SEMS.application.internal.commandservices;

import com.backendsems.SEMS.domain.model.aggregates.Notification;
import com.backendsems.SEMS.domain.model.commands.CreateNotificationCommand;
import com.backendsems.SEMS.domain.model.events.NotificationCreatedEvent;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationCommandServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private NotificationCommandServiceImpl notificationCommandService;

    @Test
    void createNotification_SavesNotificationAndPublishesEvent_WhenSuccessful() {

        DeviceId mockDeviceId = mock(DeviceId.class);
        UserId mockUserId = mock(UserId.class);

        CreateNotificationCommand command = new CreateNotificationCommand(
                mockDeviceId,
                mockUserId,
                "Alerta de Consumo Alto",
                "WARNING"
        );

        notificationCommandService.createNotification(command);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(eventPublisher, times(1)).publishEvent(any(NotificationCreatedEvent.class));
    }
}