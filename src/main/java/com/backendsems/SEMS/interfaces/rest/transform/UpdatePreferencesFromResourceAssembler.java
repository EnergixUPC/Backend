package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.commands.UpdatePreferencesCommand;
import com.backendsems.SEMS.interfaces.rest.resources.UpdatePreferencesResource;

/**
 * UpdatePreferencesFromResourceAssembler
 * Ensamblador para convertir UpdatePreferencesResource a UpdatePreferencesCommand.
 */
public class UpdatePreferencesFromResourceAssembler {

    /**
     * Convierte un UpdatePreferencesResource a UpdatePreferencesCommand.
     * @param resource El recurso de actualización.
     * @param userId El ID del usuario.
     * @return UpdatePreferencesCommand.
     */
    public static UpdatePreferencesCommand toCommand(UpdatePreferencesResource resource, Long userId) {
        return new UpdatePreferencesCommand(
                userId,
                resource.habilitarMonitoreoEnergia(),
                resource.recibirAlertasAltoConsumo(),
                resource.monitorearCalefaccionRefrigeracion(),
                resource.monitorearElectrodomesticosPrincipales(),
                resource.monitorearElectronicos(),
                resource.monitorearDispositivosCocina(),
                resource.incluirIluminacionExterior(),
                resource.rastrearEnergiaEspera(),
                resource.emailsResumenDiario(),
                resource.reportesProgresoSemanal(),
                resource.sugerirAutomizacionesAhorro(),
                resource.alertasDispositivosDesconectados()
        );
    }
}