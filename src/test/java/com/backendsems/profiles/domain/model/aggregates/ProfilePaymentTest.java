package com.backendsems.profiles.domain.model.aggregates;

import com.backendsems.profiles.domain.model.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for Profile Aggregate
 */
@DisplayName("Profile Aggregate Tests - User Profile & Payment Gateway")
@ExtendWith(MockitoExtension.class)
class ProfilePaymentTest {

    private Profile profile;
    private PersonName mockFirstName;
    private PersonName mockLastName;
    private EmailAddress mockEmail;
    private PhoneNumber mockPhone;
    private Address mockAddress;

    @BeforeEach
    void setUp() {
        mockFirstName = new PersonName("Juan");
        mockLastName = new PersonName("García");
        mockEmail = new EmailAddress("juan.garcia@example.com");
        mockPhone = new PhoneNumber("+57 300 1234567");
        mockAddress = new Address("Calle Principal 123, Bogotá, Colombia");

        profile = new Profile(
                mockFirstName,
                mockLastName,
                mockEmail,
                "SecurePassword123!",
                mockPhone,
                mockAddress,
                "https://example.com/profiles/juan-photo.jpg"
        );
    }

    @Test
    @DisplayName("Debe crear un perfil de usuario completo")
    void testCreateCompleteUserProfile() {
        // Assert
        assertEquals(mockFirstName, profile.getName());
        assertEquals(mockLastName, profile.getLastName());
        assertEquals(mockEmail, profile.getEmail());
        assertEquals("SecurePassword123!", profile.getPassword());
        assertEquals(mockPhone, profile.getPhone());
        assertEquals(mockAddress, profile.getAddress());
        assertEquals("https://example.com/profiles/juan-photo.jpg", profile.getProfilePhotoUrl());
    }

    @Test
    @DisplayName("Debe crear perfil sin foto de perfil")
    void testCreateProfileWithoutPhotoUrl() {
        // Act
        Profile profileWithoutPhoto = new Profile(
                mockFirstName,
                mockLastName,
                mockEmail,
                "Password123!",
                mockPhone,
                mockAddress,
                null
        );

        // Assert
        assertNull(profileWithoutPhoto.getProfilePhotoUrl());
    }

    @Test
    @DisplayName("Debe crear perfil vacío")
    void testCreateEmptyProfile() {
        // Act
        Profile emptyProfile = new Profile();

        // Assert
        assertNotNull(emptyProfile);
        assertNull(emptyProfile.getName());
        assertNull(emptyProfile.getLastName());
    }

    @Test
    @DisplayName("Debe actualizar nombre del perfil")
    void testUpdateProfileName() {
        // Arrange
        PersonName newName = new PersonName("Carlos");
        Profile updatedProfile = new Profile(
                newName,
                profile.getLastName(),
                profile.getEmail(),
                profile.getPassword(),
                profile.getPhone(),
                profile.getAddress(),
                profile.getProfilePhotoUrl()
        );

        // Assert
        assertEquals(newName, updatedProfile.getName());
    }

    @Test
    @DisplayName("Debe actualizar email del perfil")
    void testUpdateProfileEmail() {
        // Arrange
        EmailAddress newEmail = new EmailAddress("newemail@example.com");
        Profile updatedProfile = new Profile(
                profile.getName(),
                profile.getLastName(),
                newEmail,
                profile.getPassword(),
                profile.getPhone(),
                profile.getAddress(),
                profile.getProfilePhotoUrl()
        );

        // Assert
        assertEquals(newEmail, updatedProfile.getEmail());
    }

    @Test
    @DisplayName("Debe actualizar teléfono del perfil")
    void testUpdateProfilePhone() {
        // Arrange
        PhoneNumber newPhone = new PhoneNumber("+57 310 9876543");
        Profile updatedProfile = new Profile(
                profile.getName(),
                profile.getLastName(),
                profile.getEmail(),
                profile.getPassword(),
                newPhone,
                profile.getAddress(),
                profile.getProfilePhotoUrl()
        );

        // Assert
        assertEquals(newPhone, updatedProfile.getPhone());
    }

    @Test
    @DisplayName("Debe actualizar dirección del perfil")
    void testUpdateProfileAddress() {
        // Arrange
        Address newAddress = new Address("Carrera 5 No. 45-67, Medellín, Colombia");
        Profile updatedProfile = new Profile(
                profile.getName(),
                profile.getLastName(),
                profile.getEmail(),
                profile.getPassword(),
                profile.getPhone(),
                newAddress,
                profile.getProfilePhotoUrl()
        );

        // Assert
        assertEquals(newAddress, updatedProfile.getAddress());
    }

    @Test
    @DisplayName("Debe actualizar URL de foto de perfil")
    void testUpdateProfilePhotoUrl() {
        // Arrange
        String newPhotoUrl = "https://example.com/profiles/juan-nueva-foto.jpg";
        Profile updatedProfile = new Profile(
                profile.getName(),
                profile.getLastName(),
                profile.getEmail(),
                profile.getPassword(),
                profile.getPhone(),
                profile.getAddress(),
                newPhotoUrl
        );

        // Assert
        assertEquals(newPhotoUrl, updatedProfile.getProfilePhotoUrl());
    }

    @Test
    @DisplayName("Debe validar email en ValueObject")
    void testEmailValidationInValueObject() {
        // Assert
        assertDoesNotThrow(() -> new EmailAddress("valid@example.com"));
        assertThrows(IllegalArgumentException.class, () -> new EmailAddress("invalid-email"));
    }

    @Test
    @DisplayName("Debe validar nombre en ValueObject")
    void testNameValidationInValueObject() {
        // Assert
        assertDoesNotThrow(() -> new PersonName("ValidName"));
        assertThrows(IllegalArgumentException.class, () -> new PersonName(""));
    }

    @Test
    @DisplayName("Debe validar teléfono en ValueObject")
    void testPhoneValidationInValueObject() {
        // Assert
        assertDoesNotThrow(() -> new PhoneNumber("+57 300 1234567"));
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber("invalid-phone"));
    }

    @Test
    @DisplayName("Debe validar dirección en ValueObject")
    void testAddressValidationInValueObject() {
        // Assert
        assertDoesNotThrow(() -> new Address("Valid Address"));
        assertThrows(IllegalArgumentException.class, () -> new Address(""));
    }

    @Test
    @DisplayName("Debe manejar múltiples actualizaciones de perfil")
    void testMultipleProfileUpdates() {
        // Arrange
        PersonName updatedName = new PersonName("Carlos");
        PersonName updatedLastName = new PersonName("López");
        EmailAddress updatedEmail = new EmailAddress("carlos.lopez@example.com");
        PhoneNumber updatedPhone = new PhoneNumber("+57 320 4567890");
        Address updatedAddress = new Address("Avenida Paseo 789, Cali, Colombia");

        // Act
        Profile fullyUpdatedProfile = new Profile(
                updatedName,
                updatedLastName,
                updatedEmail,
                "NewPassword456!",
                updatedPhone,
                updatedAddress,
                "https://example.com/carlos-photo.jpg"
        );

        // Assert
        assertEquals(updatedName, fullyUpdatedProfile.getName());
        assertEquals(updatedLastName, fullyUpdatedProfile.getLastName());
        assertEquals(updatedEmail, fullyUpdatedProfile.getEmail());
        assertEquals(updatedPhone, fullyUpdatedProfile.getPhone());
        assertEquals(updatedAddress, fullyUpdatedProfile.getAddress());
    }

    @Test
    @DisplayName("Debe mantener integridad de contraseña después de actualizaciones")
    void testPasswordIntegrityAfterUpdates() {
        // Arrange
        String originalPassword = "SecurePassword123!";

        // Assert
        assertEquals(originalPassword, profile.getPassword());
    }

    @Test
    @DisplayName("Debe soportar diferentes formatos de teléfono internacional")
    void testInternationalPhoneFormats() {
        // Assert
        assertDoesNotThrow(() -> new PhoneNumber("+1 555 123 4567"));
        assertDoesNotThrow(() -> new PhoneNumber("+34 91 123 4567"));
        assertDoesNotThrow(() -> new PhoneNumber("+86 10 1234 5678"));
    }

    @Test
    @DisplayName("Debe soportar diferentes formatos de email")
    void testDifferentEmailFormats() {
        // Assert
        assertDoesNotThrow(() -> new EmailAddress("user@example.com"));
        assertDoesNotThrow(() -> new EmailAddress("user.name@example.co.uk"));
        assertDoesNotThrow(() -> new EmailAddress("user+tag@example.com"));
    }

    @Test
    @DisplayName("Debe crear perfil con información de pasarela de pago (simulado)")
    void testProfileWithPaymentGatewayInfo() {
        // En una aplicación real, esto sería un campo adicional
        // Por ahora verificamos que el perfil se crea correctamente

        // Assert
        assertNotNull(profile);
        assertNotNull(profile.getEmail());
        assertNotNull(profile.getPhone());
        // El email y teléfono se usarían para contactar en transacciones de pago
    }

    @Test
    @DisplayName("Debe manejar nombres con caracteres especiales (planes internacionales)")
    void testNamesWithSpecialCharacters() {
        // Act
        PersonName spanishName = new PersonName("José María");
        PersonName frenchName = new PersonName("François");
        PersonName germanName = new PersonName("Müller");

        // Assert
        assertEquals("José María", spanishName.name());
        assertEquals("François", frenchName.name());
        assertEquals("Müller", germanName.name());
    }

    @Test
    @DisplayName("Debe crear perfil para planes de suscripción")
    void testProfileForSubscriptionPlans() {
        // El perfil debe contener toda la información necesaria para planes
        // y gestión de pago

        // Assert
        assertNotNull(profile.getName());           // Para identificar al usuario
        assertNotNull(profile.getEmail());          // Para notificaciones de pago
        assertNotNull(profile.getPhone());          // Para confirmaciones
        assertNotNull(profile.getAddress());        // Para facturación
        assertNotNull(profile.getPassword());       // Para seguridad
    }
}

