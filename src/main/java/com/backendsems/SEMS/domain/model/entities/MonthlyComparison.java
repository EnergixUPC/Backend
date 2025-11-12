package com.backendsems.SEMS.domain.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "monthly_comparison")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyComparison {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToMany(mappedBy = "monthlyComparison", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MonthlyData> months;
    
    @Column(name = "current_month", length = 3)
    private String currentMonth;
    
    @Column(name = "previous_month_comparison")
    private Double previousMonthComparison;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}

// @Entity  // ENTIDAD SECUNDARIA DESHABILITADA
@Table(name = "monthly_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MonthlyData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 3)
    private String month;
    
    @Column(nullable = false)
    private Integer year;
    
    @Column(nullable = false)
    private Double consumption;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monthly_comparison_id")
    private MonthlyComparison monthlyComparison;
}