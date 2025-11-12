package com.backendsems.SEMS.application.commandhandlers;

import com.backendsems.SEMS.domain.model.entities.User;
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
     * @return el usuario actualizado
     */
    @Transactional
    public User handle(UpdateProfileCommand command) {
        // La validación se hace automáticamente en el constructor del record

        // Buscar usuario existente
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar datos del perfil
        user.setFirstName(command.firstName());
        user.setLastName(command.lastName());
        user.setPhoneNumber(command.phoneNumber());

        // Si se usa foto de perfil:
        if (command.profilePhotoUrl() != null) {
            user.setProfilePhotoUrl(command.profilePhotoUrl());
        }

        // Guardar cambios
        User updated = userRepository.save(user);
        return updated;
    }
}
