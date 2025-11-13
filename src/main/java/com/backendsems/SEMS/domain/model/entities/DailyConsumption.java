package com.backendsems.SEMS.domain.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "daily_consumption")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyConsumption {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @OneToMany(mappedBy = "dailyConsumption", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ConsumptionDataPoint> dataPoints;
    
    @Column(name = "total_consumption")
    private Double totalConsumption;
    
    @Column(name = "peak_time")
    private LocalTime peakTime;
    
    @Column(name = "peak_value")
    private Double peakValue;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}

@Entity  // REHABILITADA - NECESARIA PARA EVITAR ERRORES
@Table(name = "consumption_data_points")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ConsumptionDataPoint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalTime time;
    
    @Column(nullable = false)
    private Double value;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_consumption_id")
    private DailyConsumption dailyConsumption;
}