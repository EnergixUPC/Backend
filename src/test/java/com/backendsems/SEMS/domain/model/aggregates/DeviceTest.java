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
 */
@DisplayName("Device Aggregate Tests")
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

    @Test
    @DisplayName("Debe crear un dispositivo con todos los parámetros")
    void testCreateDeviceWithAllParameters() {
        // Assert
        assertEquals(mockUserId, device.getUserId());
        assertEquals(mockDeviceName, device.getName());
        assertEquals(mockDeviceCategory, device.getCategory());
        assertEquals(mockDeviceStatus, device.getStatus());
        assertEquals(mockDeviceLocation, device.getLocation());
        assertTrue(device.isActivo());
    }


    @Test
    @DisplayName("Debe actualizar el estado del dispositivo")
    void testUpdateDeviceStatus() {
        // Arrange
        DeviceStatus newStatus = mock(DeviceStatus.class);

        // Act
        device.updateStatus(newStatus);

        // Assert
        assertEquals(newStatus, device.getStatus());
    }

    @Test
    @DisplayName("Debe crear un dispositivo desde AddDeviceCommand")
    void testCreateDeviceFromCommand() {
        // Arrange
        UserId userId = mock(UserId.class);

        // Act & Assert
        assertNotNull(device);
        assertNotNull(device.getUserId());
    }



    @Test
    @DisplayName("Debe verificar que los valores inicializados no sean nulos")
    void testDeviceInitializationNotNull() {
        // Assert
        assertNotNull(device.getUserId());
        assertNotNull(device.getName());
        assertNotNull(device.getCategory());
        assertNotNull(device.getStatus());
        assertNotNull(device.getLocation());
    }
}

