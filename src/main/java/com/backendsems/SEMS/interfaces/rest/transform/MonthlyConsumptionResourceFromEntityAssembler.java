package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.interfaces.rest.resources.MonthlyConsumptionResource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MonthlyConsumptionResourceFromEntityAssembler {
    
    public static MonthlyConsumptionResource toResourceFromDailySummary(List<Object[]> dailySummaryData) {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());

        List<MonthlyConsumptionResource.DailyConsumptionData> dailyConsumptions = new ArrayList<>();
        double totalConsumption = 0.0;

        for (Object[] result : dailySummaryData) {
            LocalDate date = null;
            if (result[0] instanceof java.sql.Date) {
                date = ((java.sql.Date) result[0]).toLocalDate();
            } else if (result[0] instanceof LocalDate) {
                date = (LocalDate) result[0];
            }
            
            Double consumption = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;

            if (date != null) {
                dailyConsumptions.add(new MonthlyConsumptionResource.DailyConsumptionData(date, consumption));
                totalConsumption += consumption;
            }
        }

        return new MonthlyConsumptionResource(
                dailyConsumptions,
                totalConsumption,
                monthStart,
                monthEnd
        );
    }
}
