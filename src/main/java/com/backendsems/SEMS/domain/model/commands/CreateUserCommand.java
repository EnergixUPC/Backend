package com.backendsems.SEMS.domain.model.commands;

/**
 * Command para crear un nuevo User Aggregate
 */
public record CreateUserCommand(
        String email,
        String firstName,
        String lastName,
        String password
) {
    /**
     * Constructor que incluye validaciones
     */
    public CreateUserCommand {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email es requerido");
        }
        
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre es requerido");
        }
        
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Apellido es requerido");
        }
        
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password debe tener al menos 6 caracteres");
        }
    }
}