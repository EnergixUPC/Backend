package com.backendsems.iam.interfaces.acl;

import com.backendsems.iam.domain.model.commands.SignUpCommand;
import com.backendsems.iam.domain.model.entities.Role;
import com.backendsems.iam.domain.model.queries.GetUserByEmailQuery;
import com.backendsems.iam.domain.model.queries.GetUserByIdQuery;
import com.backendsems.iam.domain.services.UserCommandService;
import com.backendsems.iam.domain.services.UserQueryService;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * IAMContext
 * <p>
 *     This class is a facade for the IAM context. It provides a simple interface for other bounded contexts to interact with the
 *     IAM context.
 *     This class is a part of the ACL layer.
 * </p>
 *
 */
public class IamContext {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    public IamContext(UserCommandService userCommandService, UserQueryService userQueryService) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
    }

    /**
     * Creates a user with the given email and password.
     * @param email The email of the user.
     * @param password The password of the user.
     * @return The id of the created user.
     */
    public Long createUser(String email, String password) {
        var signUpCommand = new SignUpCommand(email, password, "", "", "", "", List.of(Role.getDefaultRole()));
        var result = userCommandService.handle(signUpCommand);
        if (result.isEmpty()) return 0L;
        return result.get().getId();
    }

    /**
     * Creates a user with the given email, password and roles.
     * @param email The email of the user.
     * @param password The password of the user.
     * @param roleNames The names of the roles of the user. When a role does not exist, it is ignored.
     * @return The id of the created user.
     */
    public Long createUser(String email, String password, List<String> roleNames) {
        List<Role> roles = roleNames.stream().map(name -> Role.toRoleFromName(name)).toList();
        var signUpCommand = new SignUpCommand(email, password, "", "", "", "", roles);
        var result = userCommandService.handle(signUpCommand);
        if (result.isEmpty()) return 0L;
        return result.get().getId();
    }

    /**
     * Fetches the id of the user with the given email.
     * @param email The email of the user.
     * @return The id of the user.
     */
    public Long fetchUserIdByEmail(String email) {
        var getUserByEmailQuery = new GetUserByEmailQuery(email);
        var result = userQueryService.handle(getUserByEmailQuery);
        if (result.isEmpty()) return 0L;
        return result.get().getId();
    }

    /**
     * Fetches the email of the user with the given id.
     * @param userId The id of the user.
     * @return The email of the user.
     */
    public String fetchEmailByUserId(Long userId) {
        var getUserByIdQuery = new GetUserByIdQuery(userId);
        var result = userQueryService.handle(getUserByIdQuery);
        if (result.isEmpty()) return Strings.EMPTY;
        return result.get().getEmail();
    }

}