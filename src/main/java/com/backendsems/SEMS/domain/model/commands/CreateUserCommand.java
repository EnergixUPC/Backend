package com.backendsems.SEMS.domain.model.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CreateUserCommand
 * Comando para crear un nuevo usuario en el sistema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserCommand {
    
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
    
    public void validate() {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email es requerido");
        }
        
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password debe tener al menos 6 caracteres");
        }
        
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre es requerido");
        }
        
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Apellido es requerido");
        }
    }
}