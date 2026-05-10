package com.backendsems.SEMS.domain.model.aggregates;

import com.backendsems.SEMS.domain.model.commands.AddDeviceCommand;
import com.backendsems.SEMS.domain.model.valueobjects.*;
import com.backendsems.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Device extends AuditableAbstractAggregateRoot<Device> {
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id"))
    private UserId userId;

    @Embedded
    private DeviceName name;

    @Embedded
    private DeviceCategory category;

    @Embedded
    private DeviceStatus status;

    @Embedded
    private DeviceLocation location;

    @Column(nullable = false)
    private boolean activo;

    public Device() {
    }

    public Device(UserId userId, DeviceName name, DeviceCategory category, DeviceStatus status, DeviceLocation location, boolean activo) {
        this.userId = userId;
        this.name = name;
        this.category = category;
        this.status = status;
        this.location = location;
        this.activo = activo;
    }

    public static Device create(AddDeviceCommand command, UserId userId) {
        return new Device(
                userId,
                new DeviceName(command.name()),
                new DeviceCategory(command.category()),
                new DeviceStatus(command.status()),
                new DeviceLocation(command.location()),
                command.active()
        );
    }

    public void updateStatus(DeviceStatus status) {
        this.status = status;
    }

    public void update(String name, String category, String status, String location, Boolean active) {
        if (name != null && !name.isBlank())       this.name     = new DeviceName(name);
        if (category != null && !category.isBlank()) this.category = new DeviceCategory(category);
        if (status != null && !status.isBlank())   this.status   = new DeviceStatus(status);
        if (location != null && !location.isBlank()) this.location = new DeviceLocation(location);
        if (active != null)                        this.activo   = active;
    }
}