package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.aggregates.UserAggregate;
import com.backendsems.SEMS.domain.model.commands.UpdateProfileCommand;
import com.backendsems.SEMS.infrastructure.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * ProfileService
 * Servicio para gestionar la actualización del perfil de usuario.
 */
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;

    /**
     * Actualiza el perfil de un usuario existente.
     */
    @Transactional
    public UserAggregate updateProfile(UpdateProfileCommand command) {
        command.validate();

        UserAggregate user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar campos del perfil (value object)
        user.getProfile().updateProfile(
                command.getFirstName(),
                command.getLastName(),
                command.getPhoneNumber()
        );

        // Si decides guardar la foto en un campo adicional del agregado, la puedes agregar así:
        // user.setProfilePhotoUrl(command.getProfilePhotoUrl());

        // Persistir cambios
        return userRepository.save(user);
    }

    /**
     * Obtiene el perfil del usuario.
     */
    public UserAggregate getProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
