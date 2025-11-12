package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.entities.User;
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
    public User updateProfile(UpdateProfileCommand command) {
        // La validación se hace automáticamente en el constructor del record

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar campos del perfil
        user.setFirstName(command.firstName());
        user.setLastName(command.lastName());
        user.setPhoneNumber(command.phoneNumber());

        // Si decides guardar la foto:
        if (command.profilePhotoUrl() != null) {
            user.setProfilePhotoUrl(command.profilePhotoUrl());
        }

        // Persistir cambios
        return userRepository.save(user);
    }

    /**
     * Obtiene el perfil del usuario.
     */
    public User getProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
