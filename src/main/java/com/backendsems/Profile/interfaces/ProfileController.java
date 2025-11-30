package com.backendsems.Profile.interfaces.rest.resources;

import com.backendsems.Profile.domain.model.aggregates.Profile;
import com.backendsems.Profile.domain.model.commands.UpdateProfileCommand;
import com.backendsems.Profile.domain.model.services.ProfileService;
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
    public ResponseEntity<Profile> getProfile(@PathVariable Long userId) {
        Profile profile = profileService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Actualiza el perfil del usuario.
     */
    @PutMapping("/{userId}")
    public ResponseEntity<Profile> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileCommand command
    ) {
        // Asignar el userId del path parameter
        command.setUserId(userId);
        Profile updatedProfile = profileService.updateProfile(command);
        return ResponseEntity.ok(updatedProfile);
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
