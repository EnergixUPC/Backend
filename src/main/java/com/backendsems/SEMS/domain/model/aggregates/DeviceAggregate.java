package com.backendsems.SEMS.domain.model.aggregates;

import com.backendsems.SEMS.domain.model.commands.CreateDeviceCommand;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceStatus;
import com.backendsems.SEMS.domain.model.valueobjects.EnergyConsumption;
import com.backendsems.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

/**
 * Device Aggregate Root
 * Representa un dispositivo en el sistema SEMS siguiendo principios DDD
 * Extiende AuditableAbstractAggregateRoot para capacidades de auditoría
 */
// @Entity  // TEMPORALMENTE DESHABILITADO PARA EVITAR DUPLICACIÓN DE TABLAS
@Getter
public class DeviceAggregate extends AuditableAbstractAggregateRoot<DeviceAggregate> {

    @Column(nullable = false)
    private String deviceName;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType deviceType;

    @Embedded
    private DeviceStatus status;

    @Embedded
    private EnergyConsumption powerConsumption;

    @Column(nullable = false)
    private Long userId;

    public enum DeviceType {
        AIR_CONDITIONER, REFRIGERATOR, WASHING_MACHINE, TELEVISION, COMPUTER, LIGHT, OTHER
    }

    /**
     * Constructor por defecto requerido por JPA
     */
    public DeviceAggregate() {
        super();
        this.status = new DeviceStatus(false);
        this.powerConsumption = new EnergyConsumption(0.0);
    }

    public DeviceAggregate(CreateDeviceCommand command) {
        this();
        this.deviceName = command.name();
        this.location = ""; // Default location
        this.deviceType = command.type();
        this.userId = command.userId();
        this.status = new DeviceStatus(false);
        this.powerConsumption = new EnergyConsumption(0.0);
    }

    public DeviceAggregate(String deviceName, String location, DeviceType deviceType, Long userId) {
        this();
        this.deviceName = deviceName;
        this.location = location;
        this.deviceType = deviceType;
        this.userId = userId;
        this.status = new DeviceStatus(false);
        this.powerConsumption = new EnergyConsumption(0.0);
    }

    // Domain Methods
    public void activate() {
        this.status = new DeviceStatus(true);
    }

    public void deactivate() {
        this.status = new DeviceStatus(false);
    }

    public void updatePowerConsumption(Double consumption) {
        if (consumption >= 0) {
            this.powerConsumption = new EnergyConsumption(consumption);
        }
    }

    public void updateLocation(String newLocation) {
        this.location = newLocation;
    }

    public boolean isActive() {
        return this.status != null && this.status.isActive();
    }

    public boolean isConsumingPower() {
        return this.isActive() && this.powerConsumption.getValue() > 0;
    }

    public boolean belongsToUser(Long userId) {
        return this.userId.equals(userId);
    }

    public String getDeviceNameValue() {
        return this.deviceName;
    }

    public String getLocationValue() {
        return this.location;
    }

    public Double getCurrentConsumption() {
        return this.powerConsumption != null ? this.powerConsumption.getValue() : 0.0;
    }
}