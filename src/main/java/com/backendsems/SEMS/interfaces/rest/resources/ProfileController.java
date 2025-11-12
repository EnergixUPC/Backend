package com.backendsems.SEMS.interfaces.rest.resources;

import com.backendsems.SEMS.domain.model.entities.User;
import com.backendsems.SEMS.domain.model.commands.UpdateProfileCommand;
import com.backendsems.SEMS.domain.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * ProfileController
 * Controlador REST para la gestión del perfil del usuario.
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileService profileService;

    /**
     * Obtiene el perfil del usuario.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<User> getProfile(@PathVariable Long userId) {
        User profile = profileService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Actualiza el perfil del usuario.
     */
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileCommand command
    ) {
        // Crear nuevo command con el userId del path parameter
        UpdateProfileCommand commandWithUserId = new UpdateProfileCommand(
                userId,
                command.firstName(),
                command.lastName(),
                command.address(),
                command.phoneNumber(),
                command.profilePhotoUrl()
        );
        User updatedUser = profileService.updateProfile(commandWithUserId);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Actualiza la foto de perfil del usuario (subida de archivo).
     */
    @PostMapping("/{userId}/photo")
    public ResponseEntity<String> uploadProfilePhoto(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file
    ) {
        // En este punto puedes manejar la lógica de guardado del archivo,
        // por ejemplo en Cloudinary, S3 o en tu servidor local.
        // De momento simulamos que se guarda con éxito.
        String message = "Foto de perfil subida correctamente para el usuario ID: " + userId;
        return ResponseEntity.ok(message);
    }
}
