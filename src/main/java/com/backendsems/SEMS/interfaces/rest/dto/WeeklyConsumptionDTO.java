package com.backendsems.SEMS.interfaces.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * WeeklyConsumptionDTO
 * DTO para el transferencia de datos de consumo semanal
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyConsumptionDTO {
    
    private Long id;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private Integer weekNumber;
    private Integer year;
    private Double totalConsumption;
    private Double averageConsumption;
    private String peakDay;
    private Double peakValue;
    private List<WeeklyConsumptionDataDTO> weeklyData;
    
    /**
     * WeeklyConsumptionDataDTO
     * DTO para los datos diarios de consumo semanal
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyConsumptionDataDTO {
        private Long id;
        private String dayOfWeek;
        private LocalDate date;
        private Double consumption;
        private Double hoursActive;
        private Integer devicesCount;
    }
}