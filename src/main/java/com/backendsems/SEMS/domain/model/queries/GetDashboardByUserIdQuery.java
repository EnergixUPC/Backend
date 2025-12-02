package com.backendsems.SEMS.domain.model.queries;

import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record GetDashboardByUserIdQuery(UserId userId) {

    public record DeviceItem(Long id, String name, String category) {}
    public record DailyItem(Instant timestamp, double kwh) {}
    public record AlertItem(String level, String message) {}

    public record DashboardData(
            double monthlySavingGoalKwh,
            double estimatedSavingsPercent,
            int activeDevices,
            double estimatedBill,
            double todaysConsumptionKwh,
            List<DailyItem> dailyConsumption,
            Map<String, Double> categoryConsumption,
            List<DeviceItem> devices,
            List<AlertItem> alerts
    ) {}
}
