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
 */
@DisplayName("Dashboard Aggregate Tests")
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

    @Test
    @DisplayName("Debe crear un dashboard válido")
    void testCreateValidDashboard() {
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
    @DisplayName("Debe inicializar timestamps correctamente")
    void testInitializeTimestampsCorrectly() {
        // Assert
        assertNotNull(dashboard.getCreatedAt());
        assertNotNull(dashboard.getUpdatedAt());
        assertNotNull(dashboard.getLastCalculatedAt());
    }

    @Test
    @DisplayName("Debe actualizar todas las métricas del dashboard")
    void testUpdateAllMetrics() {
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
    @DisplayName("Debe mantener el usuario ID después de actualizaciones")
    void testUserIdPersistenceAfterUpdates() {
        // Arrange
        Long originalUserId = dashboard.getUserId();

        // Act
        dashboard.updateMetrics(150.0, 20.0, 5, 75.0, 12.0);

        // Assert
        assertEquals(originalUserId, dashboard.getUserId());
    }

    @Test
    @DisplayName("Debe crear dashboard con valores cero")
    void testCreateDashboardWithZeroValues() {
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
    @DisplayName("Debe calcular porcentaje de ahorro con precisión")
    void testSavingsPercentagePrecision() {
        // Arrange
        Double expectedSavingsPercent = 25.75;

        // Act
        Dashboard precisionDashboard = new Dashboard(TEST_USER_ID, 100.0, expectedSavingsPercent, 3, 50.0, 8.5, "USD");

        // Assert
        assertEquals(25.75, precisionDashboard.getEstimatedSavingsPercent());
    }

    @Test
    @DisplayName("Debe manejar múltiples actualizaciones consecutivas")
    void testMultipleConsecutiveUpdates() {
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

    @Test
    @DisplayName("Debe retornar valores negativos si es necesario")
    void testNegativeValuesHandling() {
        // Act
        dashboard.updateMetrics(-10.0, -5.0, -1, -20.0, -2.0);

        // Assert
        assertEquals(-10.0, dashboard.getMonthlySavingGoalKwh());
        assertEquals(-5.0, dashboard.getEstimatedSavingsPercent());
        assertEquals(-1, dashboard.getActiveDevices());
    }


}

