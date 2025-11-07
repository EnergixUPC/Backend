package com.backendsems.SEMS.domain.model.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UpdateProfileCommand
 * Comando para actualizar la información del perfil de un usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileCommand {

    private Long userId;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String profilePhotoUrl;

    public void validate() {
        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario es requerido");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es requerido");
        }
    }
}
