package com.backendsems.SEMS.domain.model.entities;

import com.backendsems.SEMS.domain.model.aggregates.DeviceAggregate;
import com.backendsems.SEMS.domain.model.valueobjects.EnergyConsumption;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * ConsumptionReading Entity
 * Representa una lectura de consumo de energía de un dispositivo
 */
@Entity
@Table(name = "consumption_readings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ConsumptionReading {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Embedded
    private EnergyConsumption consumption;
    
    @Column(nullable = false)
    private LocalDateTime readingTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private DeviceAggregate device;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public boolean isValidReading() {
        return consumption != null && readingTime != null;
    }
    
    public boolean isRecentReading() {
        return readingTime.isAfter(LocalDateTime.now().minusHours(24));
    }
}