package com.backendsems.SEMS.application.commandhandlers;

import com.backendsems.SEMS.domain.model.aggregates.UserAggregate;
import com.backendsems.SEMS.domain.model.commands.CreateUserCommand;
import com.backendsems.SEMS.domain.model.events.UserCreatedEvent;
import com.backendsems.SEMS.domain.model.valueobjects.Email;
import com.backendsems.SEMS.domain.model.valueobjects.UserProfile;
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
    
    public UserAggregate handle(CreateUserCommand command) {
        command.validate();
        
        // Verificar que el email no exista
        if (userRepository.existsByEmail(command.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        // Crear el agregado de usuario
        UserAggregate user = UserAggregate.builder()
                .email(new Email(command.getEmail()))
                .profile(new UserProfile(command.getFirstName(), command.getLastName(), command.getPhone()))
                .password(passwordEncoder.encode(command.getPassword()))
                .role(UserAggregate.Role.valueOf(command.getRole() != null ? command.getRole() : "USER"))
                .build();
        
        // Guardar en base de datos
        UserAggregate savedUser = userRepository.save(user);
        
        // Publicar evento de dominio
        UserCreatedEvent event = UserCreatedEvent.create(
                savedUser.getId(),
                savedUser.getEmail().getValue(),
                savedUser.getProfile().getFullName()
        );
        eventPublisher.publishEvent(event);
        
        return savedUser;
    }
}