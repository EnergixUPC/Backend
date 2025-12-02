package com.backendsems.SEMS.interfaces.rest.resources;

/**
 * UpdatePreferencesResource
 * Recurso REST para actualizar preferencias de un dispositivo.
 */
public record UpdatePreferencesResource(
        Long deviceId,
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