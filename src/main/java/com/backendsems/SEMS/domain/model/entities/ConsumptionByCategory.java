package com.backendsems.SEMS.domain.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "consumption_by_category")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionByCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToMany(mappedBy = "consumptionByCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CategoryData> categories;
    
    @Column(name = "total_consumption")
    private Double totalConsumption;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}

@Entity
@Table(name = "category_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CategoryData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Double value;
    
    @Column(nullable = false)
    private Double percentage;
    
    @Column(length = 7)
    private String color;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumption_by_category_id")
    private ConsumptionByCategory consumptionByCategory;
}