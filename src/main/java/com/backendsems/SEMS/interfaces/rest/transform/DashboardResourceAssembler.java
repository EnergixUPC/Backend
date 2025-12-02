package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.queries.GetDashboardByUserIdQuery;
import com.backendsems.SEMS.interfaces.rest.resources.*;

import java.util.stream.Collectors;

public class DashboardResourceAssembler {

    public static DashboardResource toResource(GetDashboardByUserIdQuery.DashboardData data) {
        var devices = data.devices().stream()
                .map(d -> new DashboardDeviceResource(d.id(), d.name(), d.category()))
                .collect(Collectors.toList());

        var daily = data.dailyConsumption().stream()
                .map(c -> new ConsumptionByHourResource(c.timestamp(), c.kwh()))
                .collect(Collectors.toList());

        var categories = data.categoryConsumption().entrySet().stream()
                .map(e -> new CategoryConsumptionResource(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        var alerts = data.alerts().stream()
                .map(a -> new AlertResource(a.level(), a.message()))
                .collect(Collectors.toList());

        return new DashboardResource(
                data.monthlySavingGoalKwh(),
                data.estimatedSavingsPercent(),
                data.activeDevices(),
                data.estimatedBill(),
                data.todaysConsumptionKwh(),
                daily,
                categories,
                devices,
                alerts
        );
    }
}
