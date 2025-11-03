package com.backendsems.SEMS.domain.model.queries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GetUserDevicesQuery
 * Query para obtener todos los dispositivos de un usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserDevicesQuery {
    
    private Long userId;
    
    public void validate() {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID de usuario válido es requerido");
        }
    }
}