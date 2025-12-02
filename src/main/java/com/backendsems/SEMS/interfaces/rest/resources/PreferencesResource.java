package com.backendsems.SEMS.interfaces.rest.resources;

/**
 * PreferencesResource
 * Recurso REST para representar las preferencias de un dispositivo.
 */
public record PreferencesResource(
        Long id,
        String userId,
        Long deviceId,
        Double threshold,
        boolean notificationEnabled,
        // Monitoring Settings
        boolean habilitarMonitoreoEnergia,
        boolean recibirAlertasAltoConsumo,
        boolean monitorearCalefaccionRefrigeracion,
        // Device Categories
        boolean monitorearElectrodomesticosPrincipales,
        boolean monitorearElectronicos,
        boolean monitorearDispositivosCocina,
        // Additional Features
        boolean incluirIluminacionExterior,
        boolean rastrearEnergiaEspera,
        boolean emailsResumenDiario,
        // Automation & Alerts
        boolean reportesProgresoSemanal,
        boolean sugerirAutomizacionesAhorro,
        boolean alertasDispositivosDesconectados
) {
}