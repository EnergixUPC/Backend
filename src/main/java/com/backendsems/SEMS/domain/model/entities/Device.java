package com.backendsems.SEMS.domain.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Device Entity - Entidad JPA para persistencia
 */
@Entity
@Table(name = "device")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @Builder.Default
    private DeviceStatus status = DeviceStatus.OFF;
    
    @Column(name = "real_time_status")
    @Builder.Default
    private String realTimeStatus = "Off";
    
    @Column(name = "last_active")
    @Builder.Default
    private String lastActive = "Never";
    
    @Column(name = "alert_history", length = 500)
    @Builder.Default
    private String alertHistory = "No alerts";
    
    @Column(name = "energy_consumption", length = 100)
    @Builder.Default
    private String energyConsumption = "0 kWh this week";
    
    @Column(nullable = false)
    private String location;
    
    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = false;
    
    @Builder.Default
    @Column(name = "consumption_kwh")
    private Double consumptionKwh = 0.0;
    
    @Builder.Default
    @Column(name = "efficiency_rating") 
    private Integer efficiencyRating = 85;
    
    @Column(name = "brand")
    private String brand;
    
    @Column(name = "model")
    private String model;
    
    @Builder.Default
    @Column(name = "installation_date")
    private LocalDateTime installationDate = LocalDateTime.now();
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    public enum DeviceType {
        AIR_CONDITIONER("Air Conditioner"),
        REFRIGERATOR("Refrigerator"),
        TV("TV"),
        MICROWAVE("Microwave"),
        LAPTOP("Laptop"),
        SMART_SPEAKER("Smart Speaker"),
        WASHING_MACHINE("Washing Machine"),
        DISHWASHER("Dishwasher"),
        LIGHTING("Lighting"),
        HEATING("Heating"),
        SMART_METER("Smart Meter"),
        SOLAR_PANEL("Solar Panel"),
        BATTERY("Battery"),
        HVAC("HVAC"),
        OTHER("Other");
        
        private final String displayName;
        
        DeviceType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static DeviceType fromString(String type) {
            for (DeviceType deviceType : DeviceType.values()) {
                if (deviceType.name().equalsIgnoreCase(type) || 
                    deviceType.getDisplayName().equalsIgnoreCase(type)) {
                    return deviceType;
                }
            }
            return OTHER;
        }
    }
    
    public enum DeviceStatus {
        ON("On"),
        OFF("Off"),
        STANDBY("Standby"),
        CHARGING("Charging"),
        ERROR("Error"),
        MAINTENANCE("Maintenance");
        
        private final String displayName;
        
        DeviceStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Métodos de utilidad
    public void turnOn() {
        this.status = DeviceStatus.ON;
        this.isActive = true;
        this.realTimeStatus = "On";
        this.lastActive = "Now";
    }
    
    public void turnOff() {
        this.status = DeviceStatus.OFF;
        this.isActive = false;
        this.realTimeStatus = "Off";
        this.lastActive = "Just now";
    }
    
    public String getCategoryFromType() {
        return switch (this.type) {
            case AIR_CONDITIONER, HEATING, HVAC -> "Calefacción y Refrigeración";
            case REFRIGERATOR, WASHING_MACHINE, DISHWASHER, MICROWAVE -> "Electrodomésticos Principales";
            case TV, LAPTOP, SMART_SPEAKER -> "Electrónicos";
            case LIGHTING -> "Iluminación";
            case SMART_METER, SOLAR_PANEL, BATTERY -> "Otros";
            default -> "Otros";
        };
    }
}