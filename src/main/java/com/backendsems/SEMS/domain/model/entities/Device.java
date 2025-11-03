package com.backendsems.SEMS.domain.model.entities;

import com.backendsems.SEMS.domain.model.aggregates.DeviceAggregate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Device Entity - Compatibility layer
 * Mantiene compatibilidad con el frontend mientras usa DeviceAggregate internamente
 */
@Entity
@Table(name = "devices_legacy")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Device {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String category;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceStatus status;
    
    @Column(name = "real_time_status")
    private String realTimeStatus;
    
    @Column(name = "last_active")
    private String lastActive;
    
    @Column(name = "alert_history", length = 500)
    private String alertHistory;
    
    @Column(name = "energy_consumption", length = 100)
    private String energyConsumption;
    
    @Column(nullable = false)
    private String location;
    
    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // Campos adicionales del frontend SEMS
    @Builder.Default
    @Column(name = "consumption_kwh")
    private Double consumptionKwh = 0.0;
    
    @Builder.Default
    @Column(name = "efficiency_rating") 
    private Integer efficiencyRating = 85; // Porcentaje
    
    @Column(name = "brand")
    private String brand;
    
    @Column(name = "model")
    private String model;
    
    @Builder.Default
    @Column(name = "installation_date")
    private LocalDateTime installationDate = LocalDateTime.now();
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    public enum DeviceType {
        AIR_CONDITIONER,
        REFRIGERATOR,
        TV,
        MICROWAVE,
        LAPTOP,
        SMART_SPEAKER,
        WASHING_MACHINE,
        DISHWASHER,
        LIGHTING,
        HEATING,
        SMART_METER,
        SOLAR_PANEL,
        BATTERY,
        HVAC,
        OTHER
    }
    
    public enum DeviceStatus {
        ON,
        OFF,
        STANDBY,
        CHARGING,
        ERROR,
        MAINTENANCE
    }
    
    // Conversion methods to/from DeviceAggregate
    public static Device fromAggregate(DeviceAggregate aggregate, User user) {
        return Device.builder()
                .id(aggregate.getId())
                .name(aggregate.getName())
                .category(getCategoryFromType(aggregate.getType()))
                .type(DeviceType.valueOf(aggregate.getType().name()))
                .status(aggregate.getStatus().isActive() ? DeviceStatus.ON : DeviceStatus.OFF)
                .realTimeStatus(aggregate.getStatus().isActive() ? "On" : "Off")
                .lastActive(aggregate.getStatus().isActive() ? "Now" : "Offline")
                .energyConsumption(aggregate.getCurrentConsumption().toString())
                .consumptionKwh(aggregate.getCurrentConsumption().getValue())
                .isActive(aggregate.getStatus().isActive())
                .user(user)
                .build();
    }
    
    private static String getCategoryFromType(DeviceAggregate.DeviceType type) {
        return switch (type) {
            case HVAC -> "Heating & Cooling";
            case APPLIANCE -> "Major Appliances";
            case LIGHTING -> "Lighting";
            case SMART_METER -> "Monitoring";
            case SOLAR_PANEL, BATTERY -> "Energy Storage";
            default -> "Electronics";
        };
    }
    
    public DeviceAggregate toAggregate() {
        DeviceAggregate.DeviceType aggregateType = getTypeFromCategory(this.category);
        
        return DeviceAggregate.builder()
                .id(this.id)
                .name(this.name)
                .type(aggregateType)
                .status(new com.backendsems.SEMS.domain.model.valueobjects.DeviceStatus(this.status == DeviceStatus.ON))
                .currentConsumption(new com.backendsems.SEMS.domain.model.valueobjects.EnergyConsumption(this.consumptionKwh))
                .build();
    }
    
    private static DeviceAggregate.DeviceType getTypeFromCategory(String category) {
        return switch (category) {
            case "Heating & Cooling" -> DeviceAggregate.DeviceType.HVAC;
            case "Major Appliances" -> DeviceAggregate.DeviceType.APPLIANCE;
            case "Lighting" -> DeviceAggregate.DeviceType.LIGHTING;
            case "Monitoring" -> DeviceAggregate.DeviceType.SMART_METER;
            case "Energy Storage" -> DeviceAggregate.DeviceType.SOLAR_PANEL;
            default -> DeviceAggregate.DeviceType.APPLIANCE;
        };
    }
}