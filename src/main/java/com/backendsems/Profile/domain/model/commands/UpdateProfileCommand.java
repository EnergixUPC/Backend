package com.backendsems.Profile.domain.model.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UpdateProfileCommand - Comando para actualizar el perfil
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileCommand {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phoneNumber;
    private String profilePhotoUrl;
}