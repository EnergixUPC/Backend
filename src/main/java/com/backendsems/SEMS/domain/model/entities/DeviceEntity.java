package com.backendsems.SEMS.domain.model.entities;

import com.backendsems.SEMS.domain.model.aggregates.Device;
import com.backendsems.SEMS.domain.model.valueobjects.*;
import com.backendsems.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * DeviceEntity
 * Representa la entidad persistente de un dispositivo en el dominio SEMS.
 * Extiende AuditableModel para incluir campos de auditoría como createdAt y updatedAt.
 */
@Getter
@Entity
public class DeviceEntity extends AuditableModel {
    @Embedded
    @NotNull
    private UserId userId;

    @Embedded
    @NotNull
    private DeviceName name;

    @Embedded
    @NotNull
    private DeviceCategory category;

    @Embedded
    @NotNull
    private DeviceType type;

    @Embedded
    @NotNull
    private DeviceStatus status;

    @Embedded
    @NotNull
    private DeviceActivity activity;

    @Embedded
    @NotNull
    private DeviceLocation location;

    @Column(nullable = false)
    private boolean activo;

    // Constructor público sin parámetros para JPA
    public DeviceEntity() {
    }

    // Constructor para creación controlada
    public DeviceEntity(UserId userId, DeviceName name, DeviceCategory category, DeviceType type, DeviceStatus status, DeviceActivity activity, DeviceLocation location, boolean activo) {
        this.userId = userId;
        this.name = name;
        this.category = category;
        this.type = type;
        this.status = status;
        this.activity = activity;
        this.location = location;
        this.activo = activo;
    }

    // Método estático para crear desde el aggregate Device
    public static DeviceEntity fromAggregate(Device device) {
        return new DeviceEntity(
                device.getUserId(),
                device.getName(),
                device.getCategory(),
                device.getType(),
                device.getStatus(),
                device.getActivity(),
                device.getLocation(),
                device.isActivo()
        );
    }

    // Métodos de negocio para actualizar estado
    public void updateStatus(DeviceStatus status) {
        this.status = status;
    }

    public void updateActivity(DeviceActivity activity) {
        this.activity = activity;
    }
}