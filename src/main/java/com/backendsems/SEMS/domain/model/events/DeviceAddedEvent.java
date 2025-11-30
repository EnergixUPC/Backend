package com.backendsems.SEMS.domain.model.events;

import com.backendsems.SEMS.domain.model.valueobjects.*;
import lombok.Getter;

/**
 * DeviceAddedEvent
 * Evento de dominio que se dispara cuando se agrega un dispositivo.
 */
@Getter
public class DeviceAddedEvent {
    private final UserId userId;
    private final Long deviceId;
    private final DeviceName name;
    private final DeviceCategory category;
    private final DeviceType type;
    private final DeviceStatus status;
    private final DeviceActivity activity;
    private final DeviceLocation location;
    private final boolean activo;

    public DeviceAddedEvent(UserId userId, Long deviceId, DeviceName name, DeviceCategory category, DeviceType type, DeviceStatus status, DeviceActivity activity, DeviceLocation location, boolean activo) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.name = name;
        this.category = category;
        this.type = type;
        this.status = status;
        this.activity = activity;
        this.location = location;
        this.activo = activo;
    }
}