package com.backendsems.SEMS.application.internal.outboundservices.acl;

import com.backendsems.Profile.interfaces.acl.ProfilesContextFacade;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * ExternalProfileService
 * Servicio externo para interactuar con el contexto de Profile.
 */
@Service
public class ExternalProfileService {
    private final ProfilesContextFacade profilesContextFacade;

    /**
     * Constructor
     * @param profilesContextFacade Facade del contexto de Profile
     */
    public ExternalProfileService(ProfilesContextFacade profilesContextFacade) {
        this.profilesContextFacade = profilesContextFacade;
    }

    /**
     * Obtiene el Profile ID por email
     * @param email El email
     * @return Un Optional de UserId
     */
    public Optional<UserId> fetchProfileByEmail(String email) {
        var profileId = profilesContextFacade.fetchProfileIdByEmail(email);
        return profileId == 0L ? Optional.empty() : Optional.of(new UserId(profileId));
    }

    /**
     * Crea un nuevo perfil
     * @param firstName Nombre
     * @param lastName Apellido
     * @param email Email
     * @param password Contraseña
     * @param phone Teléfono
     * @param address Dirección
     * @return Un Optional de UserId
     */
    public Optional<UserId> createProfile(String firstName, String lastName, String email, String password, String phone, String address) {
        var profileId = profilesContextFacade.createProfile(firstName, lastName, email, password, phone, address);
        return profileId == 0L ? Optional.empty() : Optional.of(new UserId(profileId));
    }
}