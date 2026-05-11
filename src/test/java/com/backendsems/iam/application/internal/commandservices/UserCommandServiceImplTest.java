package com.backendsems.iam.application.internal.commandservices;

import com.backendsems.iam.domain.model.aggregates.User;
import com.backendsems.iam.domain.model.commands.SignInCommand;
import com.backendsems.iam.domain.model.commands.SignUpCommand;
import com.backendsems.iam.domain.model.entities.Role;
import com.backendsems.iam.domain.model.valueobjects.Roles;
import com.backendsems.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.backendsems.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.backendsems.iam.application.internal.outboundservices.hashing.HashingService;
import com.backendsems.iam.application.internal.outboundservices.tokens.TokenService;
import com.backendsems.profiles.interfaces.acl.ProfilesContextFacade;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("User Authentication Tests - US01, US02, US04")
@ExtendWith(MockitoExtension.class)
public class UserCommandServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private HashingService hashingService;

    @Mock
    private TokenService tokenService;

    @Mock
    private ProfilesContextFacade profilesContextFacade;

    @InjectMocks
    private UserCommandServiceImpl userCommandService;

    private SignUpCommand signUpCommand;

    @BeforeEach
    void setUp() {
        signUpCommand = new SignUpCommand(
                "test@energix.com",
                "Password123!",
                "Joan",
                "Teves",
                "987654321",
                "Santa Catalina",
                List.of("ROLE_USER")
        );
    }

    // ==================== US01: Registro de cuenta ====================

    @Test
    @DisplayName("US01 - Registro de cuenta - Escenario 1: Registro válido - Debe crear cuenta con datos completos y válidos")
    void US01_handle_SignUpCommand_ReturnsUserOptional_WhenSuccessful() {
        // Arrange
        Role defaultRole = new Role(Roles.ROLE_USER);
        when(roleRepository.findByName(Roles.ROLE_USER)).thenReturn(Optional.of(defaultRole));
        when(hashingService.encode(anyString())).thenReturn("hashed_password_123");

        User expectedUser = new User("test@energix.com", "hashed_password_123", "Joan", "Teves", "987654321", "Santa Catalina", List.of(defaultRole));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(expectedUser));


        Optional<User> result = userCommandService.handle(signUpCommand);


        assertTrue(result.isPresent());

        assertEquals("test@energix.com", result.get().getEmail());

        verify(userRepository, times(1)).save(any(User.class));
        verify(profilesContextFacade, times(1)).createProfile(
                "Joan", "Teves", "test@energix.com", "hashed_password_123", "987654321", "Santa Catalina"
        );
    }

    @Test
    @DisplayName("US01 - Registro de cuenta - Escenario 3: Correo duplicado - Debe rechazar registro con correo existente")
    void US01_handle_SignUpCommand_ThrowsException_WhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail("test@energix.com")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userCommandService.handle(signUpCommand);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("US01 - Registro de cuenta - Escenario 2: Datos inválidos - Debe rechazar registro con rol inexistente")
    void US01_handle_SignUpCommand_ThrowsException_WhenRoleNotFound() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(Roles.ROLE_USER)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userCommandService.handle(signUpCommand);
        });
    }

    // ==================== US02: Inicio de sesión ====================

    @Test
    @DisplayName("US02 - Inicio de sesión - Escenario 1: Credenciales válidas - Debe conceder acceso con credenciales correctas")
    void US02_handle_SignInCommand_ReturnsUserAndToken_WhenCredentialsAreValid() {
        // Arrange
        SignInCommand signInCommand = new SignInCommand("test@energix.com", "Password123!");
        User user = new User("test@energix.com", "hashed_password_123");
        when(userRepository.findByEmail("test@energix.com")).thenReturn(Optional.of(user));
        when(hashingService.matches("Password123!", "hashed_password_123")).thenReturn(true);
        when(tokenService.generateToken("test@energix.com")).thenReturn("jwt_token_abc");

        // Act
        Optional<ImmutablePair<User, String>> result = userCommandService.handle(signInCommand);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test@energix.com", result.get().getLeft().getEmail());
        assertEquals("jwt_token_abc", result.get().getRight());
    }

    @Test
    @DisplayName("US02 - Inicio de sesión - Escenario 2: Contraseña incorrecta - Debe rechazar acceso y mostrar error")
    void US02_handle_SignInCommand_ThrowsException_WhenPasswordIsIncorrect() {
        // Arrange
        SignInCommand signInCommand = new SignInCommand("test@energix.com", "WrongPassword");
        User user = new User("test@energix.com", "hashed_password_123");
        when(userRepository.findByEmail("test@energix.com")).thenReturn(Optional.of(user));
        when(hashingService.matches("WrongPassword", "hashed_password_123")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userCommandService.handle(signInCommand);
        });

        assertEquals("Invalid password", exception.getMessage());
    }

    @Test
    @DisplayName("US02 - Inicio de sesión - Escenario 3: Cuenta inexistente - Debe rechazar acceso cuando el correo no está registrado")
    void US02_handle_SignInCommand_ThrowsException_WhenUserNotFound() {
        // Arrange
        SignInCommand signInCommand = new SignInCommand("nonexistent@energix.com", "Password123!");
        when(userRepository.findByEmail("nonexistent@energix.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userCommandService.handle(signInCommand);
        });

        assertEquals("User not found", exception.getMessage());
    }

    // ==================== US04: Cerrar sesión de manera segura ====================

    @Test
    @DisplayName("US04 - Cerrar sesión de manera segura - Escenario 1: El token debe ser generado para poder ser invalidado al cerrar sesión")
    void US04_tokenIsGenerated_ForSessionManagement() {
        // Arrange - Verifica que el sistema genera tokens que pueden ser invalidados
        SignInCommand signInCommand = new SignInCommand("test@energix.com", "Password123!");
        User user = new User("test@energix.com", "hashed_password_123");
        when(userRepository.findByEmail("test@energix.com")).thenReturn(Optional.of(user));
        when(hashingService.matches("Password123!", "hashed_password_123")).thenReturn(true);
        when(tokenService.generateToken("test@energix.com")).thenReturn("jwt_token_to_invalidate");

        // Act
        Optional<ImmutablePair<User, String>> result = userCommandService.handle(signInCommand);

        // Assert - El token existe y puede ser invalidado al cerrar sesión
        assertTrue(result.isPresent());
        assertNotNull(result.get().getRight());
        assertFalse(result.get().getRight().isEmpty());
        verify(tokenService, times(1)).generateToken("test@energix.com");
    }
}