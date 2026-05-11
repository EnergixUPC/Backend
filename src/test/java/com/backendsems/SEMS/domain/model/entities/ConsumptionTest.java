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
 * Covers US06 (Monitorear consumo en tiempo real), US09 (Consultar reporte semanal),
 * US10 (Comparar consumo entre periodos), and US12 (Consultar historial de consumo mensual)
 */
@DisplayName("Consumption Entity Tests - US06, US09, US10, US12")
@ExtendWith(MockitoExtension.class)
class ConsumptionTest {

    private Consumption consumption;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now();
        consumption = new Consumption(50.5, "device-001", testDateTime, ConsumptionStatus.ON);
    }

    // ==================== US06: Monitorear consumo en tiempo real ====================

    @Test
    @DisplayName("US06 - Monitorear consumo en tiempo real - Escenario 1: Consumo actualizado - Debe crear un consumo válido con datos actualizados")
    void US06_testCreateValidConsumption() {
        // Assert
        assertNotNull(consumption);
        assertEquals(50.5, consumption.getConsumption());
        assertEquals("device-001", consumption.getDeviceId());
        assertEquals(testDateTime, consumption.getCalculatedAt());
        assertEquals(ConsumptionStatus.ON, consumption.getStatus());
    }

    @Test
    @DisplayName("US06 - Monitorear consumo en tiempo real - Escenario 1: Consumo actualizado - Debe actualizar el consumo correctamente")
    void US06_testUpdateConsumption() {
        // Arrange
        Double newConsumption = 75.3;

        // Act
        consumption.updateConsumption(newConsumption);

        // Assert
        assertEquals(75.3, consumption.getConsumption());
    }

    @Test
    @DisplayName("US06 - Monitorear consumo en tiempo real - Escenario 2: Dispositivo desconectado - Debe actualizar el estado del consumo a OFF")
    void US06_testUpdateConsumptionStatus() {
        // Arrange
        consumption.updateStatus(ConsumptionStatus.OFF);

        // Assert
        assertEquals(ConsumptionStatus.OFF, consumption.getStatus());
    }

    @Test
    @DisplayName("US06 - Monitorear consumo en tiempo real - Debe actualizar la fecha de cálculo")
    void US06_testUpdateCalculatedAt() {
        // Arrange
        LocalDateTime newDateTime = LocalDateTime.now().plusHours(1);

        // Act
        consumption.updateCalculatedAt(newDateTime);

        // Assert
        assertEquals(newDateTime, consumption.getCalculatedAt());
    }

    @Test
    @DisplayName("US06 - Monitorear consumo en tiempo real - Debe manejar diferentes estados de consumo (ON/OFF)")
    void US06_testConsumptionStatusTransitions() {
        // Arrange
        consumption.updateStatus(ConsumptionStatus.ON);
        assertEquals(ConsumptionStatus.ON, consumption.getStatus());

        // Act & Assert
        consumption.updateStatus(ConsumptionStatus.OFF);
        assertEquals(ConsumptionStatus.OFF, consumption.getStatus());

        consumption.updateStatus(ConsumptionStatus.ON);
        assertEquals(ConsumptionStatus.ON, consumption.getStatus());
    }

    // ==================== US09: Consultar reporte semanal de consumo ====================

    @Test
    @DisplayName("US09 - Consultar reporte semanal de consumo - Escenario 1: Generación automática del reporte - Debe crear consumo con cero kWh para dispositivos sin uso")
    void US09_testCreateConsumptionWithZeroKwh() {
        // Act
        Consumption zeroConsumption = new Consumption(0.0, "device-002", testDateTime, ConsumptionStatus.ON);

        // Assert
        assertEquals(0.0, zeroConsumption.getConsumption());
    }

    @Test
    @DisplayName("US09 - Consultar reporte semanal de consumo - Escenario 1: Generación automática del reporte - Debe crear consumo con valores altos de kWh")
    void US09_testCreateConsumptionWithHighValues() {
        // Act
        Consumption highConsumption = new Consumption(1000.5, "device-003", testDateTime, ConsumptionStatus.ON);

        // Assert
        assertEquals(1000.5, highConsumption.getConsumption());
    }

    // ==================== US10: Comparar consumo entre periodos ====================

    @Test
    @DisplayName("US10 - Comparar consumo entre periodos - Escenario 1: Comparación entre semanas - Debe mantener la precisión decimal en consumos para comparaciones exactas")
    void US10_testConsumptionDecimalPrecision() {
        // Arrange
        Double preciseConsumption = 123.456789;

        // Act
        consumption.updateConsumption(preciseConsumption);

        // Assert
        assertEquals(123.456789, consumption.getConsumption());
    }

    @Test
    @DisplayName("US10 - Comparar consumo entre periodos - Debe permitir comparar consumos de diferentes periodos por deviceId")
    void US10_testConsumptionPeriodsComparison() {
        // Arrange - Simular consumos en dos periodos diferentes
        LocalDateTime week1 = LocalDateTime.of(2026, 5, 1, 10, 0);
        LocalDateTime week2 = LocalDateTime.of(2026, 5, 8, 10, 0);

        Consumption consumptionWeek1 = new Consumption(120.0, "device-001", week1, ConsumptionStatus.ON);
        Consumption consumptionWeek2 = new Consumption(95.0, "device-001", week2, ConsumptionStatus.ON);

        // Assert - Se pueden comparar valores entre periodos
        assertTrue(consumptionWeek1.getConsumption() > consumptionWeek2.getConsumption());
        assertEquals("device-001", consumptionWeek1.getDeviceId());
        assertEquals("device-001", consumptionWeek2.getDeviceId());
        assertNotEquals(consumptionWeek1.getCalculatedAt(), consumptionWeek2.getCalculatedAt());
    }

    // ==================== US12: Consultar historial de consumo mensual ====================

    @Test
    @DisplayName("US12 - Consultar historial de consumo mensual - Escenario 1: Visualización de historial - Debe inicializar consumo vacío correctamente")
    void US12_testCreateEmptyConsumption() {
        // Act
        Consumption emptyConsumption = new Consumption();

        // Assert
        assertNotNull(emptyConsumption);
        assertNull(emptyConsumption.getConsumption());
        assertNull(emptyConsumption.getDeviceId());
    }

    @Test
    @DisplayName("US12 - Consultar historial de consumo mensual - Debe almacenar fecha de cálculo para historial mensual")
    void US12_testConsumptionTimestampForMonthlyHistory() {
        // Arrange
        LocalDateTime january = LocalDateTime.of(2026, 1, 15, 12, 0);
        LocalDateTime february = LocalDateTime.of(2026, 2, 15, 12, 0);

        // Act
        Consumption janConsumption = new Consumption(200.0, "device-001", january, ConsumptionStatus.ON);
        Consumption febConsumption = new Consumption(180.0, "device-001", february, ConsumptionStatus.ON);

        // Assert
        assertEquals(1, janConsumption.getCalculatedAt().getMonthValue());
        assertEquals(2, febConsumption.getCalculatedAt().getMonthValue());
        assertTrue(janConsumption.getConsumption() > febConsumption.getConsumption());
    }
}
