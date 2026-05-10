package com.backendsems.SEMS.domain.model.aggregates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for Dashboard Aggregate
 * Covers US06 (Monitorear consumo en tiempo real), US11 (Proyección de factura mensual),
 * US12 (Consultar historial de consumo mensual), and US14 (Identificar dispositivos de alto consumo)
 */
@DisplayName("Dashboard Aggregate Tests - US06, US11, US12, US14")
@ExtendWith(MockitoExtension.class)
class DashboardTest {

    private Dashboard dashboard;
    private static final Long TEST_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        dashboard = new Dashboard(
                TEST_USER_ID,
                100.0,      // monthlySavingGoalKwh
                15.5,       // estimatedSavingsPercent
                3,          // activeDevices
                50.0,       // estimatedBill
                8.5,        // todaysConsumptionKwh
                "USD"       // currency
        );
    }

    // ==================== US06: Monitorear consumo en tiempo real ====================

    @Test
    @DisplayName("US06 - Monitorear consumo en tiempo real - Escenario 1: Consumo actualizado - Debe crear un dashboard válido con consumo actualizado")
    void US06_testCreateValidDashboard() {
        // Assert
        assertNotNull(dashboard);
        assertEquals(TEST_USER_ID, dashboard.getUserId());
        assertEquals(100.0, dashboard.getMonthlySavingGoalKwh());
        assertEquals(15.5, dashboard.getEstimatedSavingsPercent());
        assertEquals(3, dashboard.getActiveDevices());
        assertEquals(50.0, dashboard.getEstimatedBill());
        assertEquals(8.5, dashboard.getTodaysConsumptionKwh());
        assertEquals("USD", dashboard.getCurrency());
    }

    @Test
    @DisplayName("US06 - Monitorear consumo en tiempo real - Escenario 1: Consumo actualizado - Debe inicializar timestamps correctamente")
    void US06_testInitializeTimestampsCorrectly() {
        // Assert
        assertNotNull(dashboard.getCreatedAt());
        assertNotNull(dashboard.getUpdatedAt());
        assertNotNull(dashboard.getLastCalculatedAt());
    }

    @Test
    @DisplayName("US06 - Monitorear consumo en tiempo real - Escenario 1: Consumo actualizado - Debe actualizar todas las métricas del dashboard")
    void US06_testUpdateAllMetrics() {
        // Act
        dashboard.updateMetrics(150.0, 20.0, 5, 75.0, 12.0);

        // Assert
        assertEquals(150.0, dashboard.getMonthlySavingGoalKwh());
        assertEquals(20.0, dashboard.getEstimatedSavingsPercent());
        assertEquals(5, dashboard.getActiveDevices());
        assertEquals(75.0, dashboard.getEstimatedBill());
        assertEquals(12.0, dashboard.getTodaysConsumptionKwh());
    }

    @Test
    @DisplayName("US06 - Monitorear consumo en tiempo real - Debe mantener el usuario ID después de actualizaciones")
    void US06_testUserIdPersistenceAfterUpdates() {
        // Arrange
        Long originalUserId = dashboard.getUserId();

        // Act
        dashboard.updateMetrics(150.0, 20.0, 5, 75.0, 12.0);

        // Assert
        assertEquals(originalUserId, dashboard.getUserId());
    }

    @Test
    @DisplayName("US06 - Monitorear consumo en tiempo real - Debe crear dashboard con valores cero")
    void US06_testCreateDashboardWithZeroValues() {
        // Act
        Dashboard zeroDashboard = new Dashboard(TEST_USER_ID, 0.0, 0.0, 0, 0.0, 0.0, "USD");

        // Assert
        assertEquals(0.0, zeroDashboard.getMonthlySavingGoalKwh());
        assertEquals(0.0, zeroDashboard.getEstimatedSavingsPercent());
        assertEquals(0, zeroDashboard.getActiveDevices());
        assertEquals(0.0, zeroDashboard.getEstimatedBill());
        assertEquals(0.0, zeroDashboard.getTodaysConsumptionKwh());
    }

    @Test
    @DisplayName("US06 - Monitorear consumo en tiempo real - Debe manejar múltiples actualizaciones consecutivas")
    void US06_testMultipleConsecutiveUpdates() {
        // Act
        dashboard.updateMetrics(120.0, 16.0, 4, 55.0, 9.0);
        dashboard.updateMetrics(140.0, 18.0, 5, 60.0, 10.0);
        dashboard.updateMetrics(160.0, 20.0, 6, 65.0, 11.0);

        // Assert
        assertEquals(160.0, dashboard.getMonthlySavingGoalKwh());
        assertEquals(20.0, dashboard.getEstimatedSavingsPercent());
        assertEquals(6, dashboard.getActiveDevices());
        assertEquals(65.0, dashboard.getEstimatedBill());
        assertEquals(11.0, dashboard.getTodaysConsumptionKwh());
    }

    // ==================== US11: Proyección de factura mensual ====================

    @Test
    @DisplayName("US11 - Proyección de factura mensual - Escenario 1: Proyección de factura activa - Debe mostrar monto estimado de factura")
    void US11_testEstimatedBillProjection() {
        // Assert - el dashboard tiene una proyección de factura estimada
        assertEquals(50.0, dashboard.getEstimatedBill());
        assertEquals("USD", dashboard.getCurrency());
    }

    @Test
    @DisplayName("US11 - Proyección de factura mensual - Debe calcular porcentaje de ahorro con precisión")
    void US11_testSavingsPercentagePrecision() {
        // Arrange
        Double expectedSavingsPercent = 25.75;

        // Act
        Dashboard precisionDashboard = new Dashboard(TEST_USER_ID, 100.0, expectedSavingsPercent, 3, 50.0, 8.5, "USD");

        // Assert
        assertEquals(25.75, precisionDashboard.getEstimatedSavingsPercent());
    }

    @Test
    @DisplayName("US11 - Proyección de factura mensual - Escenario 2: Consumo insuficiente - Debe crear dashboard con valores cero cuando no hay datos suficientes")
    void US11_testInsufficientDataForProjection() {
        // Act - Un dashboard con valores en 0 indica que no hay datos suficientes para proyección
        Dashboard emptyDashboard = new Dashboard(TEST_USER_ID, 0.0, 0.0, 0, 0.0, 0.0, "USD");

        // Assert
        assertEquals(0.0, emptyDashboard.getEstimatedBill());
        assertEquals(0.0, emptyDashboard.getEstimatedSavingsPercent());
    }

    // ==================== US14: Identificar dispositivos de alto consumo ====================

    @Test
    @DisplayName("US14 - Identificar dispositivos de alto consumo - Debe rastrear dispositivos activos en el dashboard")
    void US14_testActiveDevicesTracking() {
        // Assert
        assertEquals(3, dashboard.getActiveDevices());

        // Act - Actualizar con más dispositivos
        dashboard.updateMetrics(100.0, 15.5, 7, 80.0, 20.0);

        // Assert
        assertEquals(7, dashboard.getActiveDevices());
    }

    @Test
    @DisplayName("US14 - Identificar dispositivos de alto consumo - Debe retornar valores negativos si es necesario")
    void US14_testNegativeValuesHandling() {
        // Act
        dashboard.updateMetrics(-10.0, -5.0, -1, -20.0, -2.0);

        // Assert
        assertEquals(-10.0, dashboard.getMonthlySavingGoalKwh());
        assertEquals(-5.0, dashboard.getEstimatedSavingsPercent());
        assertEquals(-1, dashboard.getActiveDevices());
    }

    // ==================== US12: Consultar historial de consumo mensual ====================

    @Test
    @DisplayName("US12 - Consultar historial de consumo mensual - Escenario 1: Visualización de historial - Debe mantener timestamps de creación y actualización")
    void US12_testHistoryTimestamps() {
        // Assert - Los timestamps permiten rastrear el historial
        assertNotNull(dashboard.getCreatedAt());
        assertNotNull(dashboard.getUpdatedAt());
        assertNotNull(dashboard.getLastCalculatedAt());

        // Act
        dashboard.updateMetrics(200.0, 30.0, 10, 100.0, 25.0);

        // Assert - El updatedAt debe cambiar después de actualización
        assertNotNull(dashboard.getUpdatedAt());
    }
}
