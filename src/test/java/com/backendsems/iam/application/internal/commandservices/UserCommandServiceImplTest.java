package com.backendsems.iam.application.internal.commandservices;

import com.backendsems.iam.domain.model.aggregates.User;
import com.backendsems.iam.domain.model.commands.SignUpCommand;
import com.backendsems.iam.domain.model.entities.Role;
import com.backendsems.iam.domain.model.valueobjects.Roles;
import com.backendsems.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.backendsems.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.backendsems.iam.application.internal.outboundservices.hashing.HashingService;
import com.backendsems.profiles.interfaces.acl.ProfilesContextFacade;
import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(MockitoExtension.class)
public class UserCommandServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private HashingService hashingService;



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

    @Test
    void handle_SignUpCommand_ReturnsUserOptional_WhenSuccessful() {
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
}