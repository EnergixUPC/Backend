package com.backendsems.SEMS.domain.model.entities;

import com.backendsems.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "consumption")
public class Consumption extends AuditableModel {

    @Column(nullable = false)
    @NotNull
    private Double consumption;

    @Column(nullable = false, length = 36)
    @NotNull
    private String deviceId;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime calculatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private ConsumptionStatus status;

    public Consumption() {
    }

    public Consumption(Double consumption, String deviceId, LocalDateTime calculatedAt, ConsumptionStatus status) {
        this.consumption = consumption;
        this.deviceId = deviceId;
        this.calculatedAt = calculatedAt;
        this.status = status;
    }

    public void updateConsumption(Double consumption) {
        this.consumption = consumption;
    }

    public void updateStatus(ConsumptionStatus status) {
        this.status = status;
    }

    public void updateCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
}

