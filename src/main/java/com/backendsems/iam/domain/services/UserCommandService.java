package com.backendsems.iam.domain.services;

import com.backendsems.iam.domain.model.aggregates.User;
import com.backendsems.iam.domain.model.commands.SignInCommand;
import com.backendsems.iam.domain.model.commands.SignUpCommand;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Optional;

/**
 * UserCommandService - Servicio de dominio para comandos de usuario
 */
public interface UserCommandService {

    Optional<ImmutablePair<User, String>> handle(SignInCommand command);

    Optional<User> handle(SignUpCommand command);
}