package com.backendsems.SEMS.interfaces.rest.resources;

import java.util.List;

public record DashboardResource(
        double monthlySavingGoalKwh,
        double estimatedSavingsPercent,
        int activeDevices,
        double estimatedBill,
        double todaysConsumptionKwh,
        List<ConsumptionByHourResource> dailyConsumption,
        List<CategoryConsumptionResource> categoryConsumption,
        List<DashboardDeviceResource> devices,
        List<AlertResource> alerts
) {}
