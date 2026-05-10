package com.backendsems.SEMS.domain.model.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for DevicePreference Entity
 * Covers US08 (Configurar umbrales de alerta personalizados)
 */
@DisplayName("DevicePreference Entity Tests - US08")
@ExtendWith(MockitoExtension.class)
class DevicePreferenceTest {

    private DevicePreference preferences;
    private static final Long TEST_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        preferences = new DevicePreference(
                TEST_USER_ID,
                100.0,  // threshold
                true,   // notificationEnabled
                true,   // habilitarMonitoreoEnergia
                true,   // recibirAlertasAltoConsumo
                false,  // monitorearCalefaccionRefrigeracion
                true,   // monitorearElectrodomesticosPrincipales
                true,   // monitorearElectronicos
                false,  // monitorearDispositivosCocina
                false,  // incluirIluminacionExterior
                false,  // rastrearEnergiaEspera
                true,   // emailsResumenDiario
                true,   // reportesProgresoSemanal
                false,  // sugerirAutomizacionesAhorro
                true    // alertasDispositivosDesconectados
        );
    }

    // ==================== US08: Configurar umbrales de alerta personalizados ====================

    @Test
    @DisplayName("US08 - Configurar umbrales de alerta personalizados - Escenario 1: Debe crear preferencias con umbral personalizado")
    void US08_testCreatePreferencesWithCustomThreshold() {
        // Assert
        assertNotNull(preferences);
        assertEquals(TEST_USER_ID, preferences.getUserId());
        assertEquals(100.0, preferences.getThreshold());
        assertTrue(preferences.isNotificationEnabled());
    }

    @Test
    @DisplayName("US08 - Configurar umbrales de alerta personalizados - Escenario 1: Debe actualizar el umbral de consumo")
    void US08_testUpdateThreshold() {
        // Act
        preferences.updateThreshold(200.0);

        // Assert
        assertEquals(200.0, preferences.getThreshold());
    }

    @Test
    @DisplayName("US08 - Configurar umbrales de alerta personalizados - Escenario 1: Debe habilitar/deshabilitar notificaciones")
    void US08_testUpdateNotificationEnabled() {
        // Act
        preferences.updateNotificationEnabled(false);

        // Assert
        assertFalse(preferences.isNotificationEnabled());

        // Act - Rehabilitar
        preferences.updateNotificationEnabled(true);

        // Assert
        assertTrue(preferences.isNotificationEnabled());
    }

    @Test
    @DisplayName("US08 - Configurar umbrales de alerta personalizados - Escenario 2: Restablecimiento de valores por defecto - Debe permitir actualizar alertas de alto consumo")
    void US08_testUpdateHighConsumptionAlerts() {
        // Assert - Valor inicial
        assertTrue(preferences.isRecibirAlertasAltoConsumo());

        // Act - Deshabilitar
        preferences.updateRecibirAlertasAltoConsumo(false);

        // Assert
        assertFalse(preferences.isRecibirAlertasAltoConsumo());
    }

    @Test
    @DisplayName("US08 - Configurar umbrales de alerta personalizados - Debe permitir configurar monitoreo de energía")
    void US08_testUpdateEnergyMonitoring() {
        // Assert - Valor inicial
        assertTrue(preferences.isHabilitarMonitoreoEnergia());

        // Act
        preferences.updateHabilitarMonitoreoEnergia(false);

        // Assert
        assertFalse(preferences.isHabilitarMonitoreoEnergia());
    }

    @Test
    @DisplayName("US08 - Configurar umbrales de alerta personalizados - Debe permitir configurar alertas de dispositivos desconectados")
    void US08_testUpdateDisconnectedDeviceAlerts() {
        // Assert - Valor inicial
        assertTrue(preferences.isAlertasDispositivosDesconectados());

        // Act
        preferences.updateAlertasDispositivosDesconectados(false);

        // Assert
        assertFalse(preferences.isAlertasDispositivosDesconectados());
    }

    @Test
    @DisplayName("US08 - Configurar umbrales de alerta personalizados - Debe permitir configurar categorías de dispositivos a monitorear")
    void US08_testUpdateDeviceCategories() {
        // Act & Assert - Electrodomésticos principales
        preferences.updateMonitorearElectrodomesticosPrincipales(false);
        assertFalse(preferences.isMonitorearElectrodomesticosPrincipales());

        // Act & Assert - Electrónicos
        preferences.updateMonitorearElectronicos(false);
        assertFalse(preferences.isMonitorearElectronicos());

        // Act & Assert - Dispositivos de cocina
        preferences.updateMonitorearDispositivosCocina(true);
        assertTrue(preferences.isMonitorearDispositivosCocina());
    }

    @Test
    @DisplayName("US08 - Configurar umbrales de alerta personalizados - Debe crear preferencias vacías con constructor por defecto")
    void US08_testCreateEmptyPreferences() {
        // Act
        DevicePreference emptyPreferences = new DevicePreference();

        // Assert
        assertNotNull(emptyPreferences);
    }
}
