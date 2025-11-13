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
import java.util.ArrayList;
import java.util.List;

/**
 * WeeklyConsumption Entity
 * Representa los datos de consumo energético por semana para la gráfica Weekly Consumption Trend
 */
@Entity
@Table(name = "weekly_consumption")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class WeeklyConsumption {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "week_start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "week_end_date", nullable = false) 
    private LocalDate endDate;
    
    @Column(name = "week", nullable = false)
    private String week; // formato: "2025-W45"
    
    @OneToMany(mappedBy = "weeklyConsumption", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<WeeklyConsumptionData> dataPoints = new ArrayList<>();
    
    @Column(name = "total_consumption")
    private Double totalConsumption;
    
    @Column(name = "average_consumption")
    private Double averageConsumption;
    
    @Column(name = "peak_day")
    private String peakDay;
    
    @Column(name = "peak_consumption") // Cambio de peakValue a peakConsumption
    private Double peakConsumption;
    
    @Column(name = "weekly_average")
    private Double weeklyAverage;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Domain methods
    public void calculateTotalConsumption() {
        this.totalConsumption = dataPoints.stream()
                .mapToDouble(WeeklyConsumptionData::getConsumption)
                .sum();
    }
    
    public void calculateAverageConsumption() {
        if (!dataPoints.isEmpty()) {
            this.averageConsumption = dataPoints.stream()
                    .mapToDouble(WeeklyConsumptionData::getConsumption)
                    .average()
                    .orElse(0.0);
        }
    }
    
    public void findPeakDay() {
        dataPoints.stream()
                .max((d1, d2) -> Double.compare(d1.getConsumption(), d2.getConsumption()))
                .ifPresent(peak -> {
                    this.peakDay = peak.getDay();
                    this.peakConsumption = peak.getConsumption();
                });
    }
}