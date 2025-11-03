package com.backendsems.SEMS.domain.model.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CreateDeviceCommand
 * Comando para crear un nuevo dispositivo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeviceCommand {
    
    private String name;
    private String type;
    private Long userId;
    private boolean active;
    
    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre del dispositivo es requerido");
        }
        
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de dispositivo es requerido");
        }
        
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID de usuario válido es requerido");
        }
    }
}