package com.backendsems.SEMS.domain.model.aggregates;

import com.backendsems.SEMS.domain.model.commands.AddDeviceCommand;
import com.backendsems.SEMS.domain.model.entities.DeviceEntity;
import com.backendsems.SEMS.domain.model.valueobjects.*;
import com.backendsems.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Device extends AuditableAbstractAggregateRoot<Device> {
    @Embedded
    private UserId userId;

    @Embedded
    private DeviceName name;

    @Embedded
    private DeviceCategory category;

    @Embedded
    private DeviceType type;

    @Embedded
    private DeviceStatus status;

    @Embedded
    private DeviceActivity activity;

    @Embedded
    private DeviceLocation location;

    @Column(nullable = false)
    private boolean activo;

    public Device() {
    }

    public Device(UserId userId, DeviceName name, DeviceCategory category, DeviceType type, DeviceStatus status, DeviceActivity activity, DeviceLocation location, boolean activo) {
        this.userId = userId;
        this.name = name;
        this.category = category;
        this.type = type;
        this.status = status;
        this.activity = activity;
        this.location = location;
        this.activo = activo;
    }

    public static Device create(AddDeviceCommand command, UserId userId) {
        return new Device(
                userId,
                new DeviceName(command.nombre()),
                new DeviceCategory(command.categoria()),
                new DeviceType(command.tipo()),
                new DeviceStatus(command.estado()),
                new DeviceActivity(command.ultimaActividad()),
                new DeviceLocation(command.ubicacion()),
                command.activo()
        );
    }

    public static Device fromEntity(DeviceEntity entity) {
        return new Device(
                entity.getUserId(),
                entity.getName(),
                entity.getCategory(),
                entity.getType(),
                entity.getStatus(),
                entity.getActivity(),
                entity.getLocation(),
                entity.isActivo()
        );
    }

    public void updateStatus(DeviceStatus status) {
        this.status = status;
    }

    public void updateActivity(DeviceActivity activity) {
        this.activity = activity;
    }
}