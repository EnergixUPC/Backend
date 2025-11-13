package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.entities.Device;
import com.backendsems.SEMS.domain.model.entities.WeeklyConsumption;
import com.backendsems.SEMS.domain.model.entities.WeeklyConsumptionData;
import com.backendsems.SEMS.infrastructure.repositories.DeviceRepository;
import com.backendsems.SEMS.infrastructure.repositories.WeeklyConsumptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ReportsService
 * Servicio para el manejo de reportes de consumo energético
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportsService {

    private final WeeklyConsumptionRepository weeklyConsumptionRepository;
    private final DeviceRepository deviceRepository;

    /**
     * Obtiene el consumo semanal de un usuario
     */
    public List<WeeklyConsumption> getWeeklyConsumption(Long userId) {
        return weeklyConsumptionRepository.findByUserIdOrderByDateDesc(userId);
    }

    /**
     * Obtiene el consumo semanal de un usuario para un año específico
     */
    public List<WeeklyConsumption> getWeeklyConsumptionByYear(Long userId, Integer year) {
        return weeklyConsumptionRepository.findByUserIdAndYear(userId, year.toString());
    }

    /**
     * Obtiene el consumo semanal más reciente (últimas N semanas)
     */
    public List<WeeklyConsumption> getRecentWeeklyConsumption(Long userId, Integer weeks) {
        List<WeeklyConsumption> allRecords = weeklyConsumptionRepository.findRecentWeeklyConsumption(userId);
        return allRecords.stream()
                .limit(weeks)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el consumo semanal por rango de fechas
     */
    public List<WeeklyConsumption> getWeeklyConsumptionByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return weeklyConsumptionRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    /**
     * Obtiene estadísticas del consumo semanal para el dashboard
     */
    public Map<String, Object> getWeeklyConsumptionStats(Long userId) {
        int currentYear = LocalDate.now().getYear();
        
        // Obtener estadísticas básicas
        Double averageWeekly = weeklyConsumptionRepository.getAverageWeeklyConsumption(userId, String.valueOf(currentYear));
        Double totalYearly = weeklyConsumptionRepository.getTotalYearlyConsumption(userId, String.valueOf(currentYear));
        Optional<WeeklyConsumption> peakWeek = weeklyConsumptionRepository.findPeakWeekByUserId(userId);
        
        // Obtener últimas 8 semanas para el gráfico
        List<WeeklyConsumption> recentWeeks = getRecentWeeklyConsumption(userId, 8);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("averageWeeklyConsumption", averageWeekly != null ? averageWeekly : 0.0);
        stats.put("totalYearlyConsumption", totalYearly != null ? totalYearly : 0.0);
        stats.put("peakWeekConsumption", peakWeek.map(WeeklyConsumption::getTotalConsumption).orElse(0.0));
        stats.put("peakWeekDate", peakWeek.map(w -> w.getStartDate().toString()).orElse(""));
        stats.put("recentWeeks", recentWeeks);
        stats.put("year", currentYear);
        
        return stats;
    }

    /**
     * Obtiene el ranking de dispositivos por consumo para un período
     */
    public List<Map<String, Object>> getDeviceRanking(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Device> devices = deviceRepository.findByUserId(userId);
        
        return devices.stream()
                .map(device -> {
                    Map<String, Object> deviceData = new HashMap<>();
                    deviceData.put("id", device.getId());
                    deviceData.put("name", device.getName());
                    deviceData.put("type", device.getType().toString());
                    deviceData.put("totalConsumption", device.getConsumptionKwh());
                    deviceData.put("isActive", device.getIsActive());
                    return deviceData;
                })
                .sorted((d1, d2) -> Double.compare(
                        (Double) d2.get("totalConsumption"), 
                        (Double) d1.get("totalConsumption")))
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo registro de consumo semanal
     */
    @Transactional
    public WeeklyConsumption createWeeklyConsumption(Long userId, LocalDate weekStartDate) {
        LocalDate weekEndDate = weekStartDate.plusDays(6);
        int weekNumber = weekStartDate.get(WeekFields.ISO.weekOfYear());
        int year = weekStartDate.getYear();
        String week = String.format("%d-W%02d", year, weekNumber);

        // Crear WeeklyConsumption sin User (se manejará por userId)
        WeeklyConsumption weeklyConsumption = new WeeklyConsumption();
        weeklyConsumption.setStartDate(weekStartDate);
        weeklyConsumption.setEndDate(weekEndDate);
        weeklyConsumption.setWeek(week);
        weeklyConsumption.setDataPoints(new ArrayList<>());
        
        // Establecer el userId directamente sin crear User entity
        weeklyConsumption.setUserId(userId);

        // Generar datos dinámicos y realistas
        generateRealisticWeeklyData(weeklyConsumption, weekStartDate);

        return weeklyConsumptionRepository.save(weeklyConsumption);
    }

    /**
     * Actualiza el consumo semanal con datos reales
     */
    @Transactional
    public WeeklyConsumption updateWeeklyConsumption(Long userId, Integer year, Integer weekNumber, 
                                                     Map<String, Double> dailyConsumptions) {
        String weekString = String.format("%d-W%02d", year, weekNumber);
        WeeklyConsumption weeklyConsumption = weeklyConsumptionRepository
                .findByUserIdAndYearAndWeek(userId, weekString)
                .orElseThrow(() -> new RuntimeException("Registro de consumo semanal no encontrado"));

        // Actualizar datos diarios
        weeklyConsumption.getDataPoints().forEach(dailyData -> {
            String dayKey = dailyData.getDay().toLowerCase();
            Double consumption = dailyConsumptions.get(dayKey);
            if (consumption != null) {
                dailyData.setConsumption(consumption);
            }
        });

        // Recalcular totales y estadísticas
        weeklyConsumption.calculateTotalConsumption();
        weeklyConsumption.calculateAverageConsumption();
        weeklyConsumption.findPeakDay();

        return weeklyConsumptionRepository.save(weeklyConsumption);
    }

    /**
     * Genera datos de consumo semanal de ejemplo para testing
     * Utiliza datos completamente dinámicos y realistas
     */
    @Transactional
    public void generateSampleWeeklyData(Long userId) {
        try {
            // Crear datos para la semana actual
            LocalDate weekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
            
            // Verificar si ya existen datos para esta semana
            String weekString = String.format("%d-W%02d", weekStart.getYear(), weekStart.get(WeekFields.ISO.weekOfYear()));
            Optional<WeeklyConsumption> existing = weeklyConsumptionRepository.findByUserIdAndYearAndWeek(userId, weekString);
            
            if (existing.isPresent()) {
                updateWeeklyConsumptionWithRealisticData(existing.get());
            } else {
                createWeeklyConsumption(userId, weekStart);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error generating sample weekly data for user " + userId, e);
        }
    }

    /**
     * Calcula el porcentaje de cambio entre dos semanas
     */
    public Double calculateWeeklyChangePercentage(Long userId) {
        List<WeeklyConsumption> recentWeeks = getRecentWeeklyConsumption(userId, 2);
        
        if (recentWeeks.size() >= 2) {
            Double currentWeek = recentWeeks.get(0).getTotalConsumption();
            Double previousWeek = recentWeeks.get(1).getTotalConsumption();
            
            if (previousWeek != null && previousWeek > 0) {
                return ((currentWeek - previousWeek) / previousWeek) * 100;
            }
        }
        
        return 0.0;
    }

    /**
     * Genera datos realistas para un consumo semanal
     */
    private void generateRealisticWeeklyData(WeeklyConsumption weeklyConsumption, LocalDate weekStartDate) {
        java.util.Random random = new java.util.Random();
        double baseConsumption = generateBaseConsumption(weeklyConsumption.getUserId(), weekStartDate);
        
        // Generar datos para cada día de la semana
        for (int day = 0; day < 7; day++) {
            LocalDate currentDate = weekStartDate.plusDays(day);
            WeeklyConsumptionData dataPoint = new WeeklyConsumptionData();
            
            // Configurar información básica del día
            dataPoint.setDay(currentDate.getDayOfWeek().toString());
            dataPoint.setDate(currentDate);
            
            // Generar consumo realista
            double consumption = generateDailyConsumption(baseConsumption, day, random);
            dataPoint.setConsumption(Math.round(consumption * 10.0) / 10.0);
            
            // Generar eficiencia
            int efficiency = generateEfficiency(consumption, random);
            dataPoint.setEfficiency(efficiency);
            
            // Generar tendencia
            String trend = day == 0 ? "stable" : generateTrend(
                weeklyConsumption.getDataPoints().get(day - 1).getConsumption(), consumption);
            dataPoint.setTrend(trend);
            
            // Establecer relación con el consumo semanal
            dataPoint.setWeeklyConsumption(weeklyConsumption);
            weeklyConsumption.getDataPoints().add(dataPoint);
        }
        
        // Calcular estadísticas finales
        calculateWeeklyStatistics(weeklyConsumption);
    }

    /**
     * Genera consumo base realista basado en el usuario y la época
     */
    private double generateBaseConsumption(Long userId, LocalDate date) {
        java.util.Random random = new java.util.Random(userId + date.getDayOfYear()); // Seed consistente
        
        // Variación estacional
        double seasonalFactor = 1.0;
        int month = date.getMonthValue();
        if (month >= 12 || month <= 2) { // Invierno
            seasonalFactor = 1.2;
        } else if (month >= 6 && month <= 8) { // Verano
            seasonalFactor = 1.3;
        }
        
        // Consumo base entre 25-45 kWh por día
        return (25.0 + (random.nextDouble() * 20.0)) * seasonalFactor;
    }

    /**
     * Genera consumo diario con patrones realistas
     */
    private double generateDailyConsumption(double baseConsumption, int dayIndex, java.util.Random random) {
        double consumption = baseConsumption;
        
        // Variación diaria natural (±15%)
        double dailyVariation = -0.15 + (random.nextDouble() * 0.3);
        consumption *= (1 + dailyVariation);
        
        // Patrones de días de la semana
        switch (dayIndex) {
            case 0: // Lunes - inicio de semana
                consumption *= 0.9;
                break;
            case 1, 2, 3: // Mar, Mie, Jue - días laborales normales
                consumption *= 0.95;
                break;
            case 4: // Viernes - fin de semana laboral
                consumption *= 1.05;
                break;
            case 5, 6: // Sáb, Dom - fines de semana
                consumption *= 1.15 + (random.nextDouble() * 0.2);
                break;
        }
        
        return Math.max(10.0, consumption); // Mínimo 10 kWh
    }

    /**
     * Genera eficiencia basada en el consumo
     */
    private int generateEfficiency(double consumption, java.util.Random random) {
        // Mayor consumo tiende a menor eficiencia
        int baseEfficiency;
        if (consumption > 50) {
            baseEfficiency = 60 + random.nextInt(21); // 60-80%
        } else if (consumption > 30) {
            baseEfficiency = 70 + random.nextInt(21); // 70-90%
        } else {
            baseEfficiency = 75 + random.nextInt(16); // 75-90%
        }
        
        return Math.min(95, Math.max(50, baseEfficiency));
    }

    /**
     * Genera tendencia basada en la comparación con el día anterior
     */
    private String generateTrend(double previousConsumption, double currentConsumption) {
        double difference = currentConsumption - previousConsumption;
        double percentageChange = (difference / previousConsumption) * 100;
        
        if (percentageChange > 5) {
            return "up";
        } else if (percentageChange < -5) {
            return "down";
        } else {
            return "stable";
        }
    }

    /**
     * Calcula todas las estadísticas de la semana
     */
    private void calculateWeeklyStatistics(WeeklyConsumption weeklyConsumption) {
        weeklyConsumption.calculateTotalConsumption();
        weeklyConsumption.calculateAverageConsumption();
        weeklyConsumption.findPeakDay();
        weeklyConsumption.setWeeklyAverage(weeklyConsumption.getAverageConsumption());
    }

    /**
     * Actualiza datos existentes con valores realistas
     */
    private void updateWeeklyConsumptionWithRealisticData(WeeklyConsumption weeklyConsumption) {
        java.util.Random random = new java.util.Random();
        double baseConsumption = generateBaseConsumption(weeklyConsumption.getUserId(), weeklyConsumption.getStartDate());
        
        for (int i = 0; i < weeklyConsumption.getDataPoints().size(); i++) {
            WeeklyConsumptionData dataPoint = weeklyConsumption.getDataPoints().get(i);
            
            double consumption = generateDailyConsumption(baseConsumption, i, random);
            int efficiency = generateEfficiency(consumption, random);
            String trend = i == 0 ? "stable" : generateTrend(
                weeklyConsumption.getDataPoints().get(i-1).getConsumption(), consumption);
            
            dataPoint.setConsumption(Math.round(consumption * 10.0) / 10.0);
            dataPoint.setEfficiency(efficiency);
            dataPoint.setTrend(trend);
        }
        
        calculateWeeklyStatistics(weeklyConsumption);
        weeklyConsumptionRepository.save(weeklyConsumption);
    }
}