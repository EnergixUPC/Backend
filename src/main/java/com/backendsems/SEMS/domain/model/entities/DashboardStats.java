package com.backendsems.SEMS.domain.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dashboard_stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "energy_consumption")
    private Double energyConsumption;
    
    @Column(name = "estimated_savings")
    private Double estimatedSavings;
    
    @Column(name = "active_devices")
    private Integer activeDevices;
    
    @Column(name = "estimated_bill")
    private Double estimatedBill;
    
    @Column(name = "today_consumption")
    private Double todayConsumption;
    
    @Column(length = 10)
    private String currency;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}