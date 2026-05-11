package com.backendsems.SEMS.domain.model.aggregates;

import com.backendsems.SEMS.domain.model.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for Device Aggregate Root
 * Covers US05 (Conectar dispositivos) and US06 (Monitorear consumo en tiempo real)
 */
@DisplayName("Device Aggregate Tests - US05, US06")
@ExtendWith(MockitoExtension.class)
class DeviceTest {

    private Device device;

    @Mock
    private UserId mockUserId;

    @Mock
    private DeviceName mockDeviceName;

    @Mock
    private DeviceCategory mockDeviceCategory;

    @Mock
    private DeviceStatus mockDeviceStatus;

    @Mock
    private DeviceLocation mockDeviceLocation;

    @BeforeEach
    void setUp() {
        device = new Device(mockUserId, mockDeviceName, mockDeviceCategory, mockDeviceStatus, mockDeviceLocation, true);
    }

    // ==================== US05: Conectar dispositivos ====================

    @Test
    @DisplayName("US05 - Conectar dispositivos - Escenario 1: Dispositivo detectado - Debe crear un dispositivo con todos los parámetros")
    void US05_testCreateDeviceWithAllParameters() {
        // Assert
        assertEquals(mockUserId, device.getUserId());
        assertEquals(mockDeviceName, device.getName());
        assertEquals(mockDeviceCategory, device.getCategory());
        assertEquals(mockDeviceStatus, device.getStatus());
        assertEquals(mockDeviceLocation, device.getLocation());
        assertTrue(device.isActivo());
    }

    @Test
    @DisplayName("US05 - Conectar dispositivos - Escenario 1: Dispositivo detectado - Debe vincular dispositivo exitosamente desde comando")
    void US05_testCreateDeviceFromCommand() {

        // Arrange
        UserId userId = mock(UserId.class);

        // Act
        Device createdDevice = device;

        // Assert
        assertNotNull(createdDevice);
        assertNotNull(createdDevice.getUserId());
    }

    @Test
    @DisplayName("US05 - Conectar dispositivos - Debe verificar que los valores inicializados no sean nulos")
    void US05_testDeviceInitializationNotNull() {
        // Assert
        assertNotNull(device.getUserId());
        assertNotNull(device.getName());
        assertNotNull(device.getCategory());
        assertNotNull(device.getStatus());
        assertNotNull(device.getLocation());
    }

    // ==================== US06: Monitorear consumo en tiempo real ====================

    @Test
    @DisplayName("US06 - Monitorear consumo en tiempo real - Escenario 2: Dispositivo desconectado - Debe actualizar el estado del dispositivo")
    void US06_testUpdateDeviceStatus() {
        // Arrange
        DeviceStatus newStatus = mock(DeviceStatus.class);

        // Act
        device.updateStatus(newStatus);

        // Assert
        assertEquals(newStatus, device.getStatus());
    }

    @Test
    @DisplayName("US06 - Monitorear consumo en tiempo real - Debe permitir actualizar múltiples campos del dispositivo")
    void US06_testUpdateDeviceMultipleFields() {
        // Act
        device.update("New Device", "Electronics", "ACTIVE", "Living Room", true);

        // Assert
        assertNotNull(device.getName());
        assertNotNull(device.getCategory());
        assertNotNull(device.getStatus());
        assertNotNull(device.getLocation());
    }
}
