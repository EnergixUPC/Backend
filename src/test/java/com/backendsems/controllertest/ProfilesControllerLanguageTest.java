package com.backendsems.controllertest;

import com.backendsems.iam.application.internal.outboundservices.tokens.TokenService;
import com.backendsems.profiles.domain.model.aggregates.Profile;
import com.backendsems.profiles.domain.model.commands.UpdateProfileLanguageCommand;
import com.backendsems.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.backendsems.profiles.domain.model.queries.GetProfileByIdQuery;
import com.backendsems.profiles.domain.model.valueobjects.Address;
import com.backendsems.profiles.domain.model.valueobjects.EmailAddress;
import com.backendsems.profiles.domain.model.valueobjects.PersonName;
import com.backendsems.profiles.domain.model.valueobjects.PhoneNumber;
import com.backendsems.profiles.domain.services.ProfileCommandService;
import com.backendsems.profiles.domain.services.ProfileQueryService;
import com.backendsems.profiles.interfaces.rest.ProfilesController;
import com.backendsems.profiles.interfaces.rest.resources.ProfileResource;
import com.backendsems.profiles.interfaces.rest.resources.UpdateProfileLanguageResource;
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
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class ProfilesControllerLanguageTest {

    @Mock
    private ProfileCommandService profileCommandService;

    @Mock
    private ProfileQueryService profileQueryService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private ProfilesController profilesController;

    @Test
    void updateCurrentUserLanguage_Success() {
        UpdateProfileLanguageResource resource = new UpdateProfileLanguageResource("es");
        Profile profile = new Profile(
                new PersonName("John"),
                new PersonName("Doe"),
                new EmailAddress("john@example.com"),
                "password123",
                new PhoneNumber("123456789"),
                new Address("123 Main St"),
                null,
                "en"
        );
        
        Mockito.when(tokenService.getEmailFromToken(anyString())).thenReturn("john@example.com");
        Mockito.when(profileQueryService.handle(any(GetProfileByEmailQuery.class))).thenReturn(Optional.of(profile));
        Mockito.doNothing().when(profileCommandService).handle(any(UpdateProfileLanguageCommand.class));
        
        // Simulating the profile being updated
        Profile updatedProfile = new Profile(
                new PersonName("John"),
                new PersonName("Doe"),
                new EmailAddress("john@example.com"),
                "password123",
                new PhoneNumber("123456789"),
                new Address("123 Main St"),
                null,
                "es"
        );
        Mockito.when(profileQueryService.handle(any(GetProfileByIdQuery.class))).thenReturn(Optional.of(updatedProfile));

        ResponseEntity<ProfileResource> response = profilesController.updateCurrentUserLanguage(resource, "Bearer mockToken");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("es", response.getBody().language());
    }

    @Test
    void updateCurrentUserLanguage_ProfileNotFound() {
        UpdateProfileLanguageResource resource = new UpdateProfileLanguageResource("es");
        Mockito.when(tokenService.getEmailFromToken(anyString())).thenReturn("unknown@example.com");
        Mockito.when(profileQueryService.handle(any(GetProfileByEmailQuery.class))).thenReturn(Optional.empty());

        ResponseEntity<ProfileResource> response = profilesController.updateCurrentUserLanguage(resource, "Bearer mockToken");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
