package com.backendsems.controllertest;

import com.backendsems.iam.domain.model.aggregates.User;
import com.backendsems.iam.domain.model.commands.SignInCommand;
import com.backendsems.iam.domain.model.commands.SignUpCommand;
import com.backendsems.iam.domain.services.UserCommandService;
import com.backendsems.iam.interfaces.rest.AuthenticationController;
import com.backendsems.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.backendsems.iam.interfaces.rest.resources.SignInResource;
import com.backendsems.iam.interfaces.rest.resources.SignUpResource;
import com.backendsems.iam.interfaces.rest.resources.UserResource;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    @Mock
    private UserCommandService userCommandService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void signIn_Success() {
        SignInResource resource = new SignInResource("user@test.com", "password123");
        User user = new User("user@test.com", "password123", "John", "Doe", "123456789", "123 Main St");
        String token = "dummy.jwt.token";

        Mockito.when(userCommandService.handle(any(SignInCommand.class)))
                .thenReturn(Optional.of(new ImmutablePair<>(user, token)));

        ResponseEntity<AuthenticatedUserResource> response = authenticationController.signIn(resource);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("user@test.com", response.getBody().user().email());
        assertEquals(token, response.getBody().token());
    }

    @Test
    void signIn_NotFound() {
        SignInResource resource = new SignInResource("user@test.com", "wrongpassword");

        Mockito.when(userCommandService.handle(any(SignInCommand.class)))
                .thenReturn(Optional.empty());

        ResponseEntity<AuthenticatedUserResource> response = authenticationController.signIn(resource);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void signUp_Success() {
        SignUpResource resource = new SignUpResource("newuser@test.com", "password123", "Jane", "Doe", "987654321", "456 Elm St");
        User user = new User("newuser@test.com", "password123", "Jane", "Doe", "987654321", "456 Elm St");

        Mockito.when(userCommandService.handle(any(SignUpCommand.class)))
                .thenReturn(Optional.of(user));

        ResponseEntity<UserResource> response = authenticationController.signUp(resource);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("newuser@test.com", response.getBody().email());
    }

    @Test
    void signUp_BadRequest() {
        SignUpResource resource = new SignUpResource("baduser@test.com", "password123", "Bad", "User", "0000000", "Nowhere");

        Mockito.when(userCommandService.handle(any(SignUpCommand.class)))
                .thenReturn(Optional.empty());

        ResponseEntity<UserResource> response = authenticationController.signUp(resource);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
