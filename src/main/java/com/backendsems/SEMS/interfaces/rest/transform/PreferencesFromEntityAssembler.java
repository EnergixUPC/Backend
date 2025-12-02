package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.entities.DevicePreference;
import com.backendsems.SEMS.interfaces.rest.resources.PreferencesResource;

/**
 * PreferencesFromEntityAssembler
 * Ensamblador para convertir DevicePreference a PreferencesResource.
 */
public class PreferencesFromEntityAssembler {

    /**
     * Convierte un DevicePreference a PreferencesResource.
     * @param entity La entidad de preferencias.
     * @return PreferencesResource.
     */
    public static PreferencesResource toResource(DevicePreference entity) {
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