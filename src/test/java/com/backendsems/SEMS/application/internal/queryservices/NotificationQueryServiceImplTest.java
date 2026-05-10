package com.backendsems.SEMS.application.internal.queryservices;

import com.backendsems.SEMS.domain.model.aggregates.Notification;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.model.valueobjects.NotificationId;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for NotificationQueryService
 * Covers US07 (Generar alertas de consumo elevado - Escenario 2: Registro en historial)
 */
@DisplayName("Notification Query Service Tests - US07")
@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationQueryServiceImpl notificationQueryService;

    // ==================== US07: Generar alertas de consumo elevado ====================

    @Test
    @DisplayName("US07 - Generar alertas de consumo elevado - Escenario 2: Registro de alerta en historial - Debe consultar alertas por usuario")
    void US07_testGetNotificationsByUserId() {
        // Arrange
        UserId userId = new UserId(1L);
        DeviceId deviceId = new DeviceId(1L);
        Notification notification = new Notification(
                new NotificationId("notif-001"),
                deviceId,
                userId,
                "Alerta: consumo elevado detectado",
                "WARNING",
                LocalDateTime.now()
        );
        when(notificationRepository.findByUserId(userId)).thenReturn(List.of(notification));

        // Act
        List<Notification> result = notificationQueryService.getNotificationsByUserId(userId);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Alerta: consumo elevado detectado", result.get(0).getMessage());
        verify(notificationRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("US07 - Generar alertas de consumo elevado - Escenario 2: Registro de alerta en historial - Debe consultar alertas por dispositivo")
    void US07_testGetNotificationsByDeviceId() {
        // Arrange
        DeviceId deviceId = new DeviceId(1L);
        UserId userId = new UserId(1L);
        Notification notification = new Notification(
                new NotificationId("notif-002"),
                deviceId,
                userId,
                "Historial de alertas actualizado",
                "WARNING",
                LocalDateTime.now()
        );
        when(notificationRepository.findByDeviceId(deviceId)).thenReturn(List.of(notification));

        // Act
        List<Notification> result = notificationQueryService.getNotificationsByDeviceId(deviceId);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals("Historial de alertas actualizado", result.get(0).getMessage());
        assertNotNull(result.get(0).getTimestamp());
        verify(notificationRepository, times(1)).findByDeviceId(deviceId);
    }

    @Test
    @DisplayName("US07 - Generar alertas de consumo elevado - Debe retornar lista vacía cuando no hay alertas registradas")
    void US07_testGetAllNotifications_ReturnsEmptyList() {
        // Arrange
        when(notificationRepository.findAll()).thenReturn(List.of());

        // Act
        List<Notification> result = notificationQueryService.getAllNotifications();

        // Assert
        assertTrue(result.isEmpty());
    }
}
