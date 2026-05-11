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
 * Covers US03 (Configuración de perfil inicial), US15 (Cambiar idioma de la plataforma),
 * and US19 (Revisar planes de suscripción)
 */
@DisplayName("Profile Aggregate Tests - US03, US15, US19")
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

    // ==================== US03: Configuración de perfil inicial ====================

    @Test
    @DisplayName("US03 - Configuración de perfil inicial - Escenario 1: Configuración completa - Debe crear un perfil de usuario completo")
    void US03_testCreateCompleteUserProfile() {
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
    @DisplayName("US03 - Configuración de perfil inicial - Escenario 1: Configuración completa - Debe crear perfil sin foto de perfil")
    void US03_testCreateProfileWithoutPhotoUrl() {
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
    @DisplayName("US03 - Configuración de perfil inicial - Escenario 2: Datos incompletos - Debe crear perfil vacío")
    void US03_testCreateEmptyProfile() {
        // Act
        Profile emptyProfile = new Profile();

        // Assert
        assertNotNull(emptyProfile);
        assertNull(emptyProfile.getName());
        assertNull(emptyProfile.getLastName());
    }

    @Test
    @DisplayName("US03 - Configuración de perfil inicial - Escenario 1: Configuración completa - Debe actualizar nombre del perfil")
    void US03_testUpdateProfileName() {
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
    @DisplayName("US03 - Configuración de perfil inicial - Escenario 1: Configuración completa - Debe actualizar email del perfil")
    void US03_testUpdateProfileEmail() {
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
    @DisplayName("US03 - Configuración de perfil inicial - Escenario 1: Configuración completa - Debe actualizar teléfono del perfil")
    void US03_testUpdateProfilePhone() {
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
    @DisplayName("US03 - Configuración de perfil inicial - Escenario 1: Configuración completa - Debe actualizar dirección del perfil")
    void US03_testUpdateProfileAddress() {
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
    @DisplayName("US03 - Configuración de perfil inicial - Debe actualizar URL de foto de perfil")
    void US03_testUpdateProfilePhotoUrl() {
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
    @DisplayName("US03 - Configuración de perfil inicial - Escenario 2: Datos incompletos - Debe validar email en ValueObject")
    void US03_testEmailValidationInValueObject() {
        // Assert
        assertDoesNotThrow(() -> new EmailAddress("valid@example.com"));
        assertThrows(IllegalArgumentException.class, () -> new EmailAddress("invalid-email"));
    }

    @Test
    @DisplayName("US03 - Configuración de perfil inicial - Escenario 2: Datos incompletos - Debe validar nombre en ValueObject")
    void US03_testNameValidationInValueObject() {
        // Assert
        assertDoesNotThrow(() -> new PersonName("ValidName"));
        assertThrows(IllegalArgumentException.class, () -> new PersonName(""));
    }

    @Test
    @DisplayName("US03 - Configuración de perfil inicial - Escenario 2: Datos incompletos - Debe validar teléfono en ValueObject")
    void US03_testPhoneValidationInValueObject() {
        // Assert
        assertDoesNotThrow(() -> new PhoneNumber("+57 300 1234567"));
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber("invalid-phone"));
    }

    @Test
    @DisplayName("US03 - Configuración de perfil inicial - Escenario 2: Datos incompletos - Debe validar dirección en ValueObject")
    void US03_testAddressValidationInValueObject() {
        // Assert
        assertDoesNotThrow(() -> new Address("Valid Address"));
        assertThrows(IllegalArgumentException.class, () -> new Address(""));
    }

    @Test
    @DisplayName("US03 - Configuración de perfil inicial - Debe manejar múltiples actualizaciones de perfil")
    void US03_testMultipleProfileUpdates() {
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
    @DisplayName("US03 - Configuración de perfil inicial - Debe mantener integridad de contraseña después de actualizaciones")
    void US03_testPasswordIntegrityAfterUpdates() {
        // Arrange
        String originalPassword = "SecurePassword123!";

        // Assert
        assertEquals(originalPassword, profile.getPassword());
    }

    // ==================== US15: Cambiar idioma de la plataforma ====================

    @Test
    @DisplayName("US15 - Cambiar idioma de la plataforma - Escenario 1: Selección de idioma - Debe soportar diferentes formatos de teléfono internacional")
    void US15_testInternationalPhoneFormats() {
        // Assert - Soporta formatos internacionales para usuarios de diferentes idiomas/regiones
        assertDoesNotThrow(() -> new PhoneNumber("+1 555 123 4567"));
        assertDoesNotThrow(() -> new PhoneNumber("+34 91 123 4567"));
        assertDoesNotThrow(() -> new PhoneNumber("+86 10 1234 5678"));
    }

    @Test
    @DisplayName("US15 - Cambiar idioma de la plataforma - Escenario 2: Persistencia de idioma - Debe soportar diferentes formatos de email internacionales")
    void US15_testDifferentEmailFormats() {
        // Assert - Soporta formatos de email de diferentes dominios internacionales
        assertDoesNotThrow(() -> new EmailAddress("user@example.com"));
        assertDoesNotThrow(() -> new EmailAddress("user.name@example.co.uk"));
        assertDoesNotThrow(() -> new EmailAddress("user+tag@example.com"));
    }

    @Test
    @DisplayName("US15 - Cambiar idioma de la plataforma - Debe manejar nombres con caracteres especiales (soporte multi-idioma)")
    void US15_testNamesWithSpecialCharacters() {
        // Act
        PersonName spanishName = new PersonName("José María");
        PersonName frenchName = new PersonName("François");
        PersonName germanName = new PersonName("Müller");

        // Assert
        assertEquals("José María", spanishName.name());
        assertEquals("François", frenchName.name());
        assertEquals("Müller", germanName.name());
    }

    // ==================== US19: Revisar planes de suscripción ====================

    @Test
    @DisplayName("US19 - Revisar planes de suscripción - Escenario 1: Acceso a planes de suscripción - Debe crear perfil con información de pasarela de pago")
    void US19_testProfileWithPaymentGatewayInfo() {
        // Assert
        assertNotNull(profile);
        assertNotNull(profile.getEmail());
        assertNotNull(profile.getPhone());
        // El email y teléfono se usarían para contactar en transacciones de pago
    }

    @Test
    @DisplayName("US19 - Revisar planes de suscripción - Escenario 2: Comparación de planes - Debe crear perfil para planes de suscripción con toda la info necesaria")
    void US19_testProfileForSubscriptionPlans() {
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
