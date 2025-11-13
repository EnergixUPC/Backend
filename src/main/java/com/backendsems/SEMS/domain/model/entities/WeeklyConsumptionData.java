package com.backendsems.SEMS.domain.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * WeeklyConsumptionData Entity
 * Representa los datos de consumo de cada día de la semana
 */
@Entity
@Table(name = "weekly_consumption_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class WeeklyConsumptionData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "day", nullable = false)
    private String day; // "MON", "TUE", "WED", etc.
    
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @Column(name = "consumption", nullable = false)
    private Double consumption; // kWh
    
    @Column(name = "efficiency")
    private Integer efficiency; // Porcentaje de eficiencia
    
    @Column(name = "trend")
    private String trend; // "up", "down", "stable"
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_consumption_id", nullable = false)
    private WeeklyConsumption weeklyConsumption;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}