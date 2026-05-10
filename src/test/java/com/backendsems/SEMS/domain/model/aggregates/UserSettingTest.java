package com.backendsems.SEMS.domain.model.aggregates;

import com.backendsems.SEMS.domain.model.entities.SavingRule;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for UserSetting Aggregate
 * Covers US08 (Configurar umbrales de alerta personalizados) and US09 (Consultar reporte semanal)
 */
@DisplayName("UserSetting Aggregate Tests - US08, US09")
@ExtendWith(MockitoExtension.class)
class UserSettingTest {

    private UserSetting userSetting;
    private UserId testUserId;

    @BeforeEach
    void setUp() {
        testUserId = new UserId(1L);
        userSetting = new UserSetting(testUserId);
    }

    // ==================== US08: Configurar umbrales de alerta personalizados ====================

    @Test
    @DisplayName("US08 - Configurar umbrales de alerta personalizados - Escenario 1: Debe crear configuración con valores por defecto")
    void US08_testCreateWithDefaultValues() {
        // Assert - Valores por defecto
        assertTrue(userSetting.isNotificationsEnabled());
        assertTrue(userSetting.isHighConsumptionAlerts());
        assertTrue(userSetting.isDailyWeeklySummary());
        assertEquals(LocalTime.of(5, 0), userSetting.getNotificationScheduleStart());
        assertEquals(LocalTime.of(22, 0), userSetting.getNotificationScheduleEnd());
    }

    @Test
    @DisplayName("US08 - Configurar umbrales de alerta personalizados - Escenario 1: Debe actualizar configuración de notificaciones personalizada")
    void US08_testUpdateNotificationSettings() {
        // Act
        userSetting.updateNotificationSettings(
                false,                      // deshabilitadas
                true,                       // alertas alto consumo
                false,                      // resumen diario/semanal
                LocalTime.of(8, 0),         // desde las 8:00
                LocalTime.of(20, 0)         // hasta las 20:00
        );

        // Assert
        assertFalse(userSetting.isNotificationsEnabled());
        assertTrue(userSetting.isHighConsumptionAlerts());
        assertFalse(userSetting.isDailyWeeklySummary());
        assertEquals(LocalTime.of(8, 0), userSetting.getNotificationScheduleStart());
        assertEquals(LocalTime.of(20, 0), userSetting.getNotificationScheduleEnd());
    }

    @Test
    @DisplayName("US08 - Configurar umbrales de alerta personalizados - Escenario 2: Restablecimiento de valores por defecto - Debe restablecer configuración de seguridad")
    void US08_testUpdateSecuritySettings() {
        // Act
        userSetting.updateSecuritySettings(true);

        // Assert
        assertTrue(userSetting.isTwoFactorEnabled());

        // Act - Restablecer
        userSetting.updateSecuritySettings(false);

        // Assert
        assertFalse(userSetting.isTwoFactorEnabled());
    }

    @Test
    @DisplayName("US08 - Configurar umbrales de alerta personalizados - Debe agregar y remover reglas de ahorro")
    void US08_testAddAndRemoveSavingRules() {
        // Arrange
        SavingRule rule = new SavingRule("Apagar luces automáticamente", true);

        // Act
        userSetting.addSavingRule(rule);

        // Assert
        assertEquals(1, userSetting.getSavingRules().size());
        assertEquals("Apagar luces automáticamente", userSetting.getSavingRules().get(0).getName());

        // Act - Remover
        userSetting.removeSavingRule(rule);

        // Assert
        assertEquals(0, userSetting.getSavingRules().size());
    }

    // ==================== US09: Consultar reporte semanal de consumo ====================

    @Test
    @DisplayName("US09 - Consultar reporte semanal de consumo - Escenario 1: Generación automática - Debe crear configuración con reportes diarios habilitados por defecto")
    void US09_testDefaultReportSettings() {
        // Assert - Configuración por defecto de reportes
        assertTrue(userSetting.isReportDaily());
        assertFalse(userSetting.isReportWeekly());
        assertFalse(userSetting.isReportMonthly());
        assertTrue(userSetting.isReportFormatPdf());
        assertFalse(userSetting.isReportFormatCsv());
    }

    @Test
    @DisplayName("US09 - Consultar reporte semanal de consumo - Escenario 2: Descarga en PDF - Debe actualizar formato de reporte a PDF")
    void US09_testUpdateReportSettings() {
        // Act
        userSetting.updateReportSettings(
                false,  // daily
                true,   // weekly
                true,   // monthly
                true,   // pdf
                true    // csv
        );

        // Assert
        assertFalse(userSetting.isReportDaily());
        assertTrue(userSetting.isReportWeekly());
        assertTrue(userSetting.isReportMonthly());
        assertTrue(userSetting.isReportFormatPdf());
        assertTrue(userSetting.isReportFormatCsv());
    }

    @Test
    @DisplayName("US09 - Consultar reporte semanal de consumo - Debe crear configuración vacía con constructor por defecto")
    void US09_testEmptyConstructor() {
        // Act
        UserSetting emptySetting = new UserSetting();

        // Assert
        assertNotNull(emptySetting);
    }
}
