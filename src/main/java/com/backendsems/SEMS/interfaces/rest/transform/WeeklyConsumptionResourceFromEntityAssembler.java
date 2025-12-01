package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.interfaces.rest.resources.WeeklyConsumptionResource;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * WeeklyConsumptionResourceFromEntityAssembler
 * Ensamblador para convertir datos agregados por día a WeeklyConsumptionResource.
 */
public class WeeklyConsumptionResourceFromEntityAssembler {

    /**
     * Convierte datos agrupados por fecha a un recurso de consumo semanal.
     * @param dailySummaryData Lista de Object[] con [LocalDate, Double] de la query agrupada.
     * @return El recurso WeeklyConsumptionResource con datos reales.
     */
    public static WeeklyConsumptionResource toResourceFromDailySummary(List<Object[]> dailySummaryData) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);

        // Convertir los datos de la query a un Map para fácil acceso por fecha
        Map<LocalDate, Double> consumptionByDate = dailySummaryData.stream()
                .collect(Collectors.toMap(
                    row -> (LocalDate) row[0],  // fecha
                    row -> ((Number) row[1]).doubleValue()  // suma consumo
                ));

        List<WeeklyConsumptionResource.DailyConsumptionData> dailyData = new ArrayList<>();
        double totalWeeklyConsumption = 0.0;

        // Generar datos para cada día de la semana actual
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = weekStart.plusDays(i);
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            
            // Obtener el consumo real de ese día, o 0.0 si no hay datos
            Double dayConsumption = consumptionByDate.getOrDefault(currentDate, 0.0);
            totalWeeklyConsumption += dayConsumption;

            String dayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault());
            dailyData.add(new WeeklyConsumptionResource.DailyConsumptionData(
                currentDate,
                dayName,
                Math.round(dayConsumption * 100.0) / 100.0
            ));
        }

        return new WeeklyConsumptionResource(
            dailyData,
            Math.round(totalWeeklyConsumption * 100.0) / 100.0,
            weekStart,
            weekEnd
        );
    }

    /**
     * Método de compatibilidad hacia atrás (deprecated - usar toResourceFromDailySummary)
     * @param consumptions Lista de consumos (no se usa más)
     * @return Recurso vacío para mantener compatibilidad
     * @deprecated Usar toResourceFromDailySummary con datos agrupados
     */
    @Deprecated
    public static WeeklyConsumptionResource toResourceFromEntities(List<?> consumptions) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);
        
        // Devolver estructura vacía para mantener compatibilidad
        List<WeeklyConsumptionResource.DailyConsumptionData> emptyData = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = weekStart.plusDays(i);
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            String dayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault());
            emptyData.add(new WeeklyConsumptionResource.DailyConsumptionData(
                currentDate, dayName, 0.0
            ));
        }
        
        return new WeeklyConsumptionResource(emptyData, 0.0, weekStart, weekEnd);
    }
}