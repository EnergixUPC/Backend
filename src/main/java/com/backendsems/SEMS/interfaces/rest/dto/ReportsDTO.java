package com.backendsems.SEMS.interfaces.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ReportsDTO
 * DTO para el conjunto completo de datos de reportes
 */
public class ReportsDTO {
    
    /**
     * WeeklyConsumptionResponseDTO
     * DTO que coincide exactamente con la estructura del db.json original
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyConsumptionResponseDTO {
        private Long id;
        private String week; // "2025-W45"
        private String startDate; // "2025-11-04"
        private String endDate; // "2025-11-10"
        private List<DataPointDTO> dataPoints;
        private Double totalConsumption;
        private Double averageConsumption;
        private String peakDay;
        private Double peakConsumption;
        private Double weeklyAverage;
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class DataPointDTO {
            private String day; // "MON", "TUE", etc.
            private String date; // "2025-11-04"
            private Double consumption;
            private Integer efficiency;
            private String trend; // "up", "down", "stable"
        }
    }
    
    /**
     * CreateWeeklyConsumptionRequestDTO
     * DTO para crear nuevo registro de consumo semanal
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateWeeklyConsumptionRequestDTO {
        private String startDate; // formato: "2025-11-04"
    }
}