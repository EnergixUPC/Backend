package com.backendsems.SEMS.domain.model.queries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GetUserByIdQuery
 * Query para obtener un usuario por su ID
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserByIdQuery {
    
    private Long userId;
    
    public void validate() {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID de usuario válido es requerido");
        }
    }
}