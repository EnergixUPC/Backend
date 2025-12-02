package com.backendsems.SEMS.domain.model.aggregates;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "dashboard")
public class Dashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "monthly_saving_goal_kwh")
    private double monthlySavingGoalKwh;

    @Column(name = "estimated_savings_percent")
    private double estimatedSavingsPercent;

    @Column(name = "active_devices")
    private int activeDevices;

    @Column(name = "estimated_bill")
    private double estimatedBill;

    @Column(name = "todays_consumption_kwh")
    private double todaysConsumptionKwh;

    @Column(name = "currency")
    private String currency;

    @Column(name = "last_calculated_at")
    private Instant lastCalculatedAt;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // Constructor protegido para JPA
    protected Dashboard() {}

    // Constructor público
    public Dashboard(Long userId, double monthlySavingGoalKwh, double estimatedSavingsPercent,
                     int activeDevices, double estimatedBill, double todaysConsumptionKwh,
                     String currency) {
        this.userId = userId;
        this.monthlySavingGoalKwh = monthlySavingGoalKwh;
        this.estimatedSavingsPercent = estimatedSavingsPercent;
        this.activeDevices = activeDevices;
        this.estimatedBill = estimatedBill;
        this.todaysConsumptionKwh = todaysConsumptionKwh;
        this.currency = currency;
        this.lastCalculatedAt = Instant.now();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public double getMonthlySavingGoalKwh() { return monthlySavingGoalKwh; }
    public double getEstimatedSavingsPercent() { return estimatedSavingsPercent; }
    public int getActiveDevices() { return activeDevices; }
    public double getEstimatedBill() { return estimatedBill; }
    public double getTodaysConsumptionKwh() { return todaysConsumptionKwh; }
    public String getCurrency() { return currency; }
    public Instant getLastCalculatedAt() { return lastCalculatedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    // Método de actualización
    public void updateMetrics(double monthlySavingGoalKwh, double estimatedSavingsPercent,
                              int activeDevices, double estimatedBill, double todaysConsumptionKwh) {
        this.monthlySavingGoalKwh = monthlySavingGoalKwh;
        this.estimatedSavingsPercent = estimatedSavingsPercent;
        this.activeDevices = activeDevices;
        this.estimatedBill = estimatedBill;
        this.todaysConsumptionKwh = todaysConsumptionKwh;
        this.lastCalculatedAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
