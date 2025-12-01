package com.backendsems.SEMS.domain.model.entities;

import com.backendsems.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

/**
 * DeviceConsumptionEntity
 * Representa el registro de consumo de un dispositivo, parte del agregado Device.
 * Incluye lógica para actualizar el consumo.
 */
@Getter
@Entity
public class DeviceConsumptionEntity extends AuditableModel {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private DeviceEntity device;

    @Column(nullable = false)
    @NotNull
    private Double consumo;

    @Column(nullable = false)
    @NotNull
    private String periodo;

    @Column(nullable = true)
    private LocalDate fecha;

    // Constructor público sin parámetros para JPA
    public DeviceConsumptionEntity() {
    }

    // Constructor para creación controlada
    public DeviceConsumptionEntity(DeviceEntity device, Double consumo, String periodo) {
        this.device = device;
        this.consumo = consumo;
        this.periodo = periodo;
        this.fecha = LocalDate.now(); // Valor por defecto
    }

    // Constructor con fecha específica
    public DeviceConsumptionEntity(DeviceEntity device, Double consumo, String periodo, LocalDate fecha) {
        this.device = device;
        this.consumo = consumo;
        this.periodo = periodo;
        this.fecha = fecha != null ? fecha : LocalDate.now();
    }

    // Método de negocio para actualizar consumo
    public void updateConsumo(Double consumo) {
        this.consumo = consumo;
    }
}