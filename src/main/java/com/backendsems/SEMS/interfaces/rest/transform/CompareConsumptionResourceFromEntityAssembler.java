package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.interfaces.rest.resources.CompareConsumptionResource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CompareConsumptionResourceFromEntityAssembler {

    public static CompareConsumptionResource toResource(
            List<Object[]> dataPeriod1, LocalDate p1Start, LocalDate p1End,
            List<Object[]> dataPeriod2, LocalDate p2Start, LocalDate p2End) {

        CompareConsumptionResource.PeriodConsumptionData pd1 = buildPeriodData(dataPeriod1, p1Start, p1End);
        CompareConsumptionResource.PeriodConsumptionData pd2 = buildPeriodData(dataPeriod2, p2Start, p2End);

        double diff = pd1.totalConsumption() - pd2.totalConsumption();
        double pctDiff = 0.0;
        if (pd2.totalConsumption() > 0) {
            pctDiff = (diff / pd2.totalConsumption()) * 100.0;
        }

        return new CompareConsumptionResource(pd1, pd2, diff, pctDiff);
    }

    private static CompareConsumptionResource.PeriodConsumptionData buildPeriodData(
            List<Object[]> data, LocalDate start, LocalDate end) {
        
        List<CompareConsumptionResource.DailyConsumptionData> dailyConsumptions = new ArrayList<>();
        double totalConsumption = 0.0;

        for (Object[] result : data) {
            LocalDate date = null;
            if (result[0] instanceof java.sql.Date) {
                date = ((java.sql.Date) result[0]).toLocalDate();
            } else if (result[0] instanceof LocalDate) {
                date = (LocalDate) result[0];
            }
            
            Double consumption = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;

            if (date != null) {
                dailyConsumptions.add(new CompareConsumptionResource.DailyConsumptionData(date, consumption));
                totalConsumption += consumption;
            }
        }

        return new CompareConsumptionResource.PeriodConsumptionData(
                start, end, totalConsumption, dailyConsumptions
        );
    }
}
