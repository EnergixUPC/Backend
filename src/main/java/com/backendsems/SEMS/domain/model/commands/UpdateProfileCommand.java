package com.backendsems.SEMS.domain.model.commands;

/**
 * Command para actualizar la información del perfil de un usuario
 */
public record UpdateProfileCommand(
        Long userId,
        String firstName,
        String lastName,
        String address,
        String phoneNumber,
        String profilePhotoUrl
) {
    /**
     * Constructor que incluye validaciones
     */
    public UpdateProfileCommand {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID de usuario válido es requerido");
        }
        
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }
        
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es requerido");
        }
    }
}
