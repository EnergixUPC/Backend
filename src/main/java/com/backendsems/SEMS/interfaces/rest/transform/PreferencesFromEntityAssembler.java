package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.entities.PreferencesEntity;
import com.backendsems.SEMS.interfaces.rest.resources.PreferencesResource;

/**
 * PreferencesFromEntityAssembler
 * Ensamblador para convertir PreferencesEntity a PreferencesResource.
 */
public class PreferencesFromEntityAssembler {

    /**
     * Convierte un PreferencesEntity a PreferencesResource.
     * @param entity La entidad de preferencias.
     * @return PreferencesResource.
     */
    public static PreferencesResource toResource(PreferencesEntity entity) {
        return new PreferencesResource(
                entity.getId(),
                String.valueOf(entity.getUserId().id()),
                entity.getDevice().getId(),
                entity.getThreshold(),
                entity.isNotificationEnabled(),
                entity.isHabilitarMonitoreoEnergia(),
                entity.isRecibirAlertasAltoConsumo(),
                entity.isMonitorearCalefaccionRefrigeracion(),
                entity.isMonitorearElectrodomesticosPrincipales(),
                entity.isMonitorearElectronicos(),
                entity.isMonitorearDispositivosCocina(),
                entity.isIncluirIluminacionExterior(),
                entity.isRastrearEnergiaEspera(),
                entity.isEmailsResumenDiario(),
                entity.isReportesProgresoSemanal(),
                entity.isSugerirAutomizacionesAhorro(),
                entity.isAlertasDispositivosDesconectados()
        );
    }
}