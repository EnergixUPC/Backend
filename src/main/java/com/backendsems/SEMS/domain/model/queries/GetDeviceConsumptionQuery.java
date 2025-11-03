package com.backendsems.SEMS.domain.model.queries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * GetDeviceConsumptionQuery
 * Query para obtener el consumo de un dispositivo en un período
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDeviceConsumptionQuery {
    
    private Long deviceId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    public void validate() {
        if (deviceId == null || deviceId <= 0) {
            throw new IllegalArgumentException("ID de dispositivo válido es requerido");
        }
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30); // Por defecto últimos 30 días
        }
        
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Fecha de inicio no puede ser posterior a fecha de fin");
        }
    }
}