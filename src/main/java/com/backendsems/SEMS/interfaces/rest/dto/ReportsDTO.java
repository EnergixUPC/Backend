package com.backendsems.SEMS.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
            @Schema(description = "Día de la semana", example = "string")
            private String day;
            
            @Schema(description = "Fecha del día", example = "string")
            private String date;
            
            @Schema(description = "Consumo del día", example = "0.1")
            private Double consumption;
            
            @Schema(description = "Eficiencia del día", example = "0")
            private Integer efficiency;
            
            @Schema(description = "Tendencia del consumo", example = "string")
            private String trend;
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

    /**
     * GenerateSampleDataRequestDTO
     * DTO para generar datos de ejemplo con estructura completa
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Request para generar datos de ejemplo con estructura completa")
    public static class GenerateSampleDataRequestDTO {
        @Schema(description = "ID del registro", example = "0")
        private Long id;
        
        @Schema(description = "Semana en formato", example = "string")
        private String week;
        
        @Schema(description = "Fecha de inicio de la semana", example = "string")
        private String startDate;
        
        @Schema(description = "Fecha de fin de la semana", example = "string")
        private String endDate;
        
        @Schema(description = "Puntos de datos diarios")
        private List<WeeklyConsumptionResponseDTO.DataPointDTO> dataPoints;
        
        @Schema(description = "Consumo total de la semana", example = "0.1")
        private Double totalConsumption;
        
        @Schema(description = "Consumo promedio diario", example = "0.1")
        private Double averageConsumption;
        
        @Schema(description = "Día con mayor consumo", example = "string")
        private String peakDay;
        
        @Schema(description = "Consumo máximo del día pico", example = "0.1")
        private Double peakConsumption;
        
        @Schema(description = "Promedio semanal", example = "0.1")
        private Double weeklyAverage;
    }
}