package com.backendsems.profiles.application.acl;

import com.backendsems.profiles.domain.model.commands.CreateProfileCommand;
import com.backendsems.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.backendsems.profiles.domain.services.ProfileCommandService;
import com.backendsems.profiles.domain.services.ProfileQueryService;
import com.backendsems.profiles.interfaces.acl.ProfilesContextFacade;
import org.springframework.stereotype.Service;

/**
 * ProfilesContextFacadeImpl
 * Implementación de la fachada del contexto de perfiles
 */
@Service
public class ProfilesContextFacadeImpl implements ProfilesContextFacade {
    
    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;

    public ProfilesContextFacadeImpl(ProfileCommandService profileCommandService, 
                                    ProfileQueryService profileQueryService) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
    }

    @Override
    public Long createProfile(String firstName, String lastName, String email, 
                            String password, String phone, String address) {
        var command = new CreateProfileCommand(firstName, lastName, email, password, phone, address);
        var profile = profileCommandService.handle(command);
        return profile.map(p -> p.getId()).orElse(0L);
    }

    @Override
    public Long fetchProfileIdByEmail(String email) {
        var query = new GetProfileByEmailQuery(email);
        var profile = profileQueryService.handle(query);
        return profile.map(p -> p.getId()).orElse(null);
    }
}
