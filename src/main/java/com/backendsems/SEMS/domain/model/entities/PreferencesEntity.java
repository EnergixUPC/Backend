package com.backendsems.SEMS.domain.model.entities;

import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * PreferencesEntity
 * Representa las preferencias de un usuario para un dispositivo específico.
 */
@Getter
@Entity
public class PreferencesEntity extends AuditableModel {
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id"))
    @NotNull
    private UserId userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private DeviceEntity device;

    @Column(nullable = false)
    @NotNull
    private Double threshold;

    @Column(nullable = false)
    private boolean notificationEnabled;

    // Monitoring Settings
    @Column(nullable = false)
    private boolean habilitarMonitoreoEnergia;

    @Column(nullable = false)
    private boolean recibirAlertasAltoConsumo;

    @Column(nullable = false)
    private boolean monitorearCalefaccionRefrigeracion;

    // Device Categories
    @Column(nullable = false)
    private boolean monitorearElectrodomesticosPrincipales;

    @Column(nullable = false)
    private boolean monitorearElectronicos;

    @Column(nullable = false)
    private boolean monitorearDispositivosCocina;

    // Additional Features
    @Column(nullable = false)
    private boolean incluirIluminacionExterior;

    @Column(nullable = false)
    private boolean rastrearEnergiaEspera;

    @Column(nullable = false)
    private boolean emailsResumenDiario;

    // Automation & Alerts
    @Column(nullable = false)
    private boolean reportesProgresoSemanal;

    @Column(nullable = false)
    private boolean sugerirAutomizacionesAhorro;

    @Column(nullable = false)
    private boolean alertasDispositivosDesconectados;

    // Constructor público sin parámetros para JPA
    public PreferencesEntity() {
    }

    // Constructor para creación controlada
    public PreferencesEntity(UserId userId, DeviceEntity device, Double threshold, boolean notificationEnabled,
                             boolean habilitarMonitoreoEnergia, boolean recibirAlertasAltoConsumo,
                             boolean monitorearCalefaccionRefrigeracion, boolean monitorearElectrodomesticosPrincipales,
                             boolean monitorearElectronicos, boolean monitorearDispositivosCocina,
                             boolean incluirIluminacionExterior, boolean rastrearEnergiaEspera,
                             boolean emailsResumenDiario, boolean reportesProgresoSemanal,
                             boolean sugerirAutomizacionesAhorro, boolean alertasDispositivosDesconectados) {
        this.userId = userId;
        this.device = device;
        this.threshold = threshold;
        this.notificationEnabled = notificationEnabled;
        this.habilitarMonitoreoEnergia = habilitarMonitoreoEnergia;
        this.recibirAlertasAltoConsumo = recibirAlertasAltoConsumo;
        this.monitorearCalefaccionRefrigeracion = monitorearCalefaccionRefrigeracion;
        this.monitorearElectrodomesticosPrincipales = monitorearElectrodomesticosPrincipales;
        this.monitorearElectronicos = monitorearElectronicos;
        this.monitorearDispositivosCocina = monitorearDispositivosCocina;
        this.incluirIluminacionExterior = incluirIluminacionExterior;
        this.rastrearEnergiaEspera = rastrearEnergiaEspera;
        this.emailsResumenDiario = emailsResumenDiario;
        this.reportesProgresoSemanal = reportesProgresoSemanal;
        this.sugerirAutomizacionesAhorro = sugerirAutomizacionesAhorro;
        this.alertasDispositivosDesconectados = alertasDispositivosDesconectados;
    }

    // Métodos de negocio para actualizar preferencias
    public void updateThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public void updateNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    // Update methods for preferences
    public void updateHabilitarMonitoreoEnergia(boolean habilitarMonitoreoEnergia) {
        this.habilitarMonitoreoEnergia = habilitarMonitoreoEnergia;
    }

    public void updateRecibirAlertasAltoConsumo(boolean recibirAlertasAltoConsumo) {
        this.recibirAlertasAltoConsumo = recibirAlertasAltoConsumo;
    }

    public void updateMonitorearCalefaccionRefrigeracion(boolean monitorearCalefaccionRefrigeracion) {
        this.monitorearCalefaccionRefrigeracion = monitorearCalefaccionRefrigeracion;
    }

    public void updateMonitorearElectrodomesticosPrincipales(boolean monitorearElectrodomesticosPrincipales) {
        this.monitorearElectrodomesticosPrincipales = monitorearElectrodomesticosPrincipales;
    }

    public void updateMonitorearElectronicos(boolean monitorearElectronicos) {
        this.monitorearElectronicos = monitorearElectronicos;
    }

    public void updateMonitorearDispositivosCocina(boolean monitorearDispositivosCocina) {
        this.monitorearDispositivosCocina = monitorearDispositivosCocina;
    }

    public void updateIncluirIluminacionExterior(boolean incluirIluminacionExterior) {
        this.incluirIluminacionExterior = incluirIluminacionExterior;
    }

    public void updateRastrearEnergiaEspera(boolean rastrearEnergiaEspera) {
        this.rastrearEnergiaEspera = rastrearEnergiaEspera;
    }

    public void updateEmailsResumenDiario(boolean emailsResumenDiario) {
        this.emailsResumenDiario = emailsResumenDiario;
    }

    public void updateReportesProgresoSemanal(boolean reportesProgresoSemanal) {
        this.reportesProgresoSemanal = reportesProgresoSemanal;
    }

    public void updateSugerirAutomizacionesAhorro(boolean sugerirAutomizacionesAhorro) {
        this.sugerirAutomizacionesAhorro = sugerirAutomizacionesAhorro;
    }

    public void updateAlertasDispositivosDesconectados(boolean alertasDispositivosDesconectados) {
        this.alertasDispositivosDesconectados = alertasDispositivosDesconectados;
    }
}