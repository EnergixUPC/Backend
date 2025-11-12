package com.backendsems.SEMS.application.commandhandlers;

import com.backendsems.SEMS.domain.model.entities.User;
import com.backendsems.SEMS.domain.model.commands.CreateUserCommand;
import com.backendsems.SEMS.domain.model.events.UserCreatedEvent;
import com.backendsems.SEMS.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CreateUserCommandHandler
 * Maneja la creación de usuarios
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CreateUserCommandHandler {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    
    public User handle(CreateUserCommand command) {
        // La validación se hace automáticamente en el constructor del record
        
        // Verificar que el email no exista
        if (userRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        // Crear el usuario
        User user = User.builder()
                .email(command.email())
                .username(command.email())
                .firstName(command.firstName())
                .lastName(command.lastName())
                .phoneNumber(null) // No disponible en el command
                .password(passwordEncoder.encode(command.password()))
                .role(User.Role.USER) // Valor por defecto
                .build();
        
        // Guardar en base de datos
        User savedUser = userRepository.save(user);
        
        // Publicar evento de dominio
        UserCreatedEvent event = UserCreatedEvent.create(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName()
        );
        eventPublisher.publishEvent(event);
        
        return savedUser;
    }
}