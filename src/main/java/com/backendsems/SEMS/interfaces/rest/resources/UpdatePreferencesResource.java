package com.backendsems.SEMS.interfaces.rest.resources;

/**
 * UpdatePreferencesResource
 * Recurso REST para actualizar preferencias globales del usuario.
 */
public record UpdatePreferencesResource(
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