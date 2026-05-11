package com.backendsems.controllertest;

import com.backendsems.iam.domain.model.aggregates.User;
import com.backendsems.iam.domain.model.commands.UpdateUserPlanCommand;
import com.backendsems.iam.domain.model.queries.GetUserByIdQuery;
import com.backendsems.iam.domain.services.UserCommandService;
import com.backendsems.iam.domain.services.UserQueryService;
import com.backendsems.iam.interfaces.rest.UserController;
import com.backendsems.iam.interfaces.rest.resources.UpdateUserPlanResource;
import com.backendsems.iam.interfaces.rest.resources.UserResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private UserCommandService userCommandService;

    @InjectMocks
    private UserController userController;

    @Test
    void getUserById_Success() {
        User user = new User("user@test.com", "password123", "John", "Doe", "123456789", "123 Main St");
        Mockito.when(userQueryService.handle(any(GetUserByIdQuery.class))).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        UserResource resource = (UserResource) response.getBody();
        assertEquals("user@test.com", resource.email());
    }

    @Test
    void getUserById_NotFound() {
        Mockito.when(userQueryService.handle(any(GetUserByIdQuery.class))).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.getUserById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getAllUsers_Success() {
        User user = new User("user@test.com", "password123", "John", "Doe", "123456789", "123 Main St");
        Mockito.when(userQueryService.handleGetAll()).thenReturn(Collections.singletonList(user));

        ResponseEntity<?> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<UserResource> resources = (List<UserResource>) response.getBody();
        assertEquals(1, resources.size());
    }

    @Test
    void updateUserPlan_Success() {
        UpdateUserPlanResource resource = new UpdateUserPlanResource("premium");
        User user = new User("user@test.com", "password123", "John", "Doe", "123456789", "123 Main St");
        user.updatePlan("premium");
        
        Mockito.when(userCommandService.handle(any(UpdateUserPlanCommand.class))).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userController.updateUserPlan(1L, resource);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        UserResource responseBody = (UserResource) response.getBody();
        assertEquals("premium", responseBody.plan());
    }

    @Test
    void updateUserPlan_NotFound() {
        UpdateUserPlanResource resource = new UpdateUserPlanResource("premium");
        Mockito.when(userCommandService.handle(any(UpdateUserPlanCommand.class))).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.updateUserPlan(1L, resource);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
