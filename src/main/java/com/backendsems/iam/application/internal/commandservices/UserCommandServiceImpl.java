package com.backendsems.iam.application.internal.commandservices;

import com.backendsems.iam.application.internal.outboundservices.hashing.HashingService;
import com.backendsems.iam.application.internal.outboundservices.tokens.TokenService;
import com.backendsems.iam.domain.model.aggregates.User;
import com.backendsems.iam.domain.model.commands.SignInCommand;
import com.backendsems.iam.domain.model.commands.SignUpCommand;
import com.backendsems.iam.domain.model.commands.UpdateUserPlanCommand;
import com.backendsems.iam.domain.services.UserCommandService;
import com.backendsems.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.backendsems.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.backendsems.iam.domain.model.entities.Role;
import com.backendsems.iam.domain.model.valueobjects.Roles;
import com.backendsems.profiles.interfaces.acl.ProfilesContextFacade;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * User command service implementation
 * <p>
 *     This class implements the {@link UserCommandService} interface and provides the implementation for the
 *     {@link SignInCommand} and {@link SignUpCommand} commands.
 * </p>
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;
    private final ProfilesContextFacade profilesContextFacade;

    public UserCommandServiceImpl(UserRepository userRepository, HashingService hashingService, 
                                 TokenService tokenService, RoleRepository roleRepository,
                                 ProfilesContextFacade profilesContextFacade) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
        this.profilesContextFacade = profilesContextFacade;
    }

    @PostConstruct
    public void initRoles() {
        if (!roleRepository.existsByName(Roles.ROLE_ADMIN)) {
            roleRepository.save(new Role(Roles.ROLE_ADMIN, "Administrator role"));
        }
        if (!roleRepository.existsByName(Roles.ROLE_USER)) {
            roleRepository.save(new Role(Roles.ROLE_USER, "User role"));
        }
    }

    /**
     * Handle the sign-in command
     * <p>
     *     This method handles the {@link SignInCommand} command and returns the user and the token.
     * </p>
     * @param command the sign-in command containing the email and password
     * @return an optional containing the user matching the email and the generated token
     * @throws RuntimeException if the user is not found or the password is invalid
     */
    @Override
    public Optional<ImmutablePair<User, String>> handle(SignInCommand command) {
        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty())
            throw new RuntimeException("User not found");
        if (!hashingService.matches(command.password(), user.get().getPassword()))
            throw new RuntimeException("Invalid password");
        var token = tokenService.generateToken(user.get().getEmail());
        return Optional.of(ImmutablePair.of(user.get(), token));
    }

    /**
     * Handle the sign-up command
     * <p>
     *     This method handles the {@link SignUpCommand} command and returns the user.
     * </p>
     * @param command the sign-up command containing the email and password
     * @return the created user
     */
    @Override
    public Optional<User> handle(SignUpCommand command) {
        System.out.println("=== Starting sign-up process ===");
        System.out.println("Email: " + command.email());
        
        if (userRepository.existsByEmail(command.email())) {
            System.err.println("ERROR: Email already exists");
            throw new RuntimeException("Email already exists");
        }
        
        System.out.println("Converting role names to Role entities...");
        // Convert role names to Role entities by looking them up in the database
        var roles = command.roles().stream()
            .map(roleName -> {
                System.out.println("Looking up role: " + roleName);
                // Try to parse as Roles enum
                try {
                    var roleEnum = Roles.valueOf(roleName);
                    var role = roleRepository.findByName(roleEnum)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                    System.out.println("Role found: " + role.getName());
                    return role;
                } catch (IllegalArgumentException e) {
                    System.err.println("ERROR: Invalid role name: " + roleName);
                    throw new RuntimeException("Invalid role name: " + roleName);
                }
            })
            .toList();
        
        System.out.println("Creating user...");
        var encodedPassword = hashingService.encode(command.password());
        var user = new User(command.email(), encodedPassword, 
            command.name(), command.lastName(), command.phone(), command.address(), roles);
        userRepository.save(user);
        System.out.println("User saved successfully");
        
        // Create associated profile
        System.out.println("Creating profile...");
        try {
            Long profileId = profilesContextFacade.createProfile(
                command.name(), 
                command.lastName(), 
                command.email(), 
                encodedPassword, 
                command.phone(), 
                command.address()
            );
            System.out.println("Profile created with ID: " + profileId);
        } catch (Exception e) {
            System.err.println("ERROR creating profile: " + e.getMessage());
            e.printStackTrace();
            // Continue even if profile creation fails
        }
        
        System.out.println("=== Sign-up process completed ===");
        return userRepository.findByEmail(command.email());
    }

    /**
     * Handle the update user plan command
     * @param command the update user plan command containing the user id and the new plan
     * @return the updated user
     */
    @Override
    public Optional<User> handle(UpdateUserPlanCommand command) {
        var user = userRepository.findById(command.userId());
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        var updatedUser = user.get();
        updatedUser.updatePlan(command.plan());
        userRepository.save(updatedUser);
        return Optional.of(updatedUser);
    }
}