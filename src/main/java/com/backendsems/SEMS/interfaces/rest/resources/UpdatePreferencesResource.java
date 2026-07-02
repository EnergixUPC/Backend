package com.backendsems.SEMS.interfaces.rest.resources;

/**
 * UpdatePreferencesResource
 * Recurso REST para actualizar preferencias globales del usuario.
 */
public record UpdatePreferencesResource(
        // US21: tarifa referencial S/. por kWh (opcional; null = no se modifica / se usa el valor por defecto)
        Double pricePerKwh,
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