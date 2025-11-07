package com.backendsems.SEMS.application.commandhandlers;

import com.backendsems.SEMS.domain.model.aggregates.UserAggregate;
import com.backendsems.SEMS.domain.model.commands.UpdateProfileCommand;
import com.backendsems.SEMS.infrastructure.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * UpdateUserProfileCommandHandler
 * Se encarga de manejar el comando de actualización de perfil de usuario.
 */
@Service
@RequiredArgsConstructor
public class UpdateUserProfileCommandHandler {

    private final UserRepository userRepository;

    /**
     * Ejecuta la actualización del perfil de usuario.
     *
     * @param command comando con los datos nuevos del perfil
     * @return el agregado de usuario actualizado
     */
    @Transactional
    public UserAggregate handle(UpdateProfileCommand command) {
        command.validate();

        // Buscar usuario existente
        UserAggregate user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar datos del perfil (Value Object dentro del agregado)
        user.getProfile().updateProfile(
                command.getFirstName(),
                command.getLastName(),
                command.getPhoneNumber()
        );

        // Si se usa foto de perfil, puedes agregar:
        // user.setProfilePhotoUrl(command.getProfilePhotoUrl());

        // Guardar cambios
        UserAggregate updated = userRepository.save(user);
        return updated;
    }
}
