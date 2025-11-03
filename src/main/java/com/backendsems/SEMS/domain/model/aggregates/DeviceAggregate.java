package com.backendsems.SEMS.domain.model.aggregates;

import com.backendsems.SEMS.domain.model.entities.ConsumptionReading;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceId;
import com.backendsems.SEMS.domain.model.valueobjects.EnergyConsumption;
import com.backendsems.SEMS.domain.model.valueobjects.DeviceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Device Aggregate Root
 * Gestiona dispositivos de energía y sus lecturas
 */
@Entity
@Table(name = "devices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DeviceAggregate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType type;
    
    @Embedded
    private DeviceStatus status;
    
    @Embedded
    private EnergyConsumption currentConsumption;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserAggregate user;
    
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ConsumptionReading> consumptionReadings = new ArrayList<>();
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    public enum DeviceType {
        SMART_METER, SOLAR_PANEL, BATTERY, APPLIANCE, HVAC, LIGHTING
    }
    
    // Domain Methods
    public void turnOn() {
        this.status = new DeviceStatus(true);
    }
    
    public void turnOff() {
        this.status = new DeviceStatus(false);
    }
    
    public void addConsumptionReading(ConsumptionReading reading) {
        reading.setDevice(this);
        this.consumptionReadings.add(reading);
        // Actualizar consumo actual
        this.currentConsumption = reading.getConsumption();
    }
    
    public boolean isActive() {
        return status != null && status.isActive();
    }
    
    public DeviceId getDeviceId() {
        return new DeviceId(this.id);
    }
    
    public Double getTotalConsumption() {
        return consumptionReadings.stream()
                .mapToDouble(reading -> reading.getConsumption().getValue())
                .sum();
    }
}