package com.backendsems.SEMS.domain.model.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for Consumption Entity
 * (Reportes de Consumo de Energía)
 */
@DisplayName("Consumption Entity Tests - Energy Reports")
@ExtendWith(MockitoExtension.class)
class ConsumptionTest {

    private Consumption consumption;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now();
        consumption = new Consumption(50.5, "device-001", testDateTime, ConsumptionStatus.ON);
    }

    @Test
    @DisplayName("Debe crear un consumo válido")
    void testCreateValidConsumption() {
        // Assert
        assertNotNull(consumption);
        assertEquals(50.5, consumption.getConsumption());
        assertEquals("device-001", consumption.getDeviceId());
        assertEquals(testDateTime, consumption.getCalculatedAt());
        assertEquals(ConsumptionStatus.ON, consumption.getStatus());
    }

    @Test
    @DisplayName("Debe actualizar el consumo")
    void testUpdateConsumption() {
        // Arrange
        Double newConsumption = 75.3;

        // Act
        consumption.updateConsumption(newConsumption);

        // Assert
        assertEquals(75.3, consumption.getConsumption());
    }

    @Test
    @DisplayName("Debe actualizar el estado del consumo")
    void testUpdateConsumptionStatus() {
        // Arrange
        consumption.updateStatus(ConsumptionStatus.OFF);

        // Assert
        assertEquals(ConsumptionStatus.OFF, consumption.getStatus());
    }

    @Test
    @DisplayName("Debe actualizar la fecha de cálculo")
    void testUpdateCalculatedAt() {
        // Arrange
        LocalDateTime newDateTime = LocalDateTime.now().plusHours(1);

        // Act
        consumption.updateCalculatedAt(newDateTime);

        // Assert
        assertEquals(newDateTime, consumption.getCalculatedAt());
    }


    @Test
    @DisplayName("Debe crear consumo con cero kWh")
    void testCreateConsumptionWithZeroKwh() {
        // Act
        Consumption zeroConsumption = new Consumption(0.0, "device-002", testDateTime, ConsumptionStatus.ON);

        // Assert
        assertEquals(0.0, zeroConsumption.getConsumption());
    }

    @Test
    @DisplayName("Debe crear consumo con valores altos de kWh")
    void testCreateConsumptionWithHighValues() {
        // Act
        Consumption highConsumption = new Consumption(1000.5, "device-003", testDateTime, ConsumptionStatus.ON);

        // Assert
        assertEquals(1000.5, highConsumption.getConsumption());
    }

    @Test
    @DisplayName("Debe mantener la precisión decimal en consumos")
    void testConsumptionDecimalPrecision() {
        // Arrange
        Double preciseConsumption = 123.456789;

        // Act
        consumption.updateConsumption(preciseConsumption);

        // Assert
        assertEquals(123.456789, consumption.getConsumption());
    }

    @Test
    @DisplayName("Debe inicializar consumo vacío correctamente")
    void testCreateEmptyConsumption() {
        // Act
        Consumption emptyConsumption = new Consumption();

        // Assert
        assertNotNull(emptyConsumption);
        assertNull(emptyConsumption.getConsumption());
        assertNull(emptyConsumption.getDeviceId());
    }

    @Test
    @DisplayName("Debe manejar diferentes estados de consumo")
    void testConsumptionStatusTransitions() {
        // Arrange
        consumption.updateStatus(ConsumptionStatus.ON);
        assertEquals(ConsumptionStatus.ON, consumption.getStatus());

        // Act & Assert
        consumption.updateStatus(ConsumptionStatus.OFF);
        assertEquals(ConsumptionStatus.OFF, consumption.getStatus());

        consumption.updateStatus(ConsumptionStatus.ON);
        assertEquals(ConsumptionStatus.ON, consumption.getStatus());
    }


}

