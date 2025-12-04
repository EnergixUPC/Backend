package com.backendsems.iam.application.internal.queryservice;

import com.backendsems.iam.domain.model.aggregates.User;
import com.backendsems.iam.domain.model.queries.GetUserByEmailQuery;
import com.backendsems.iam.domain.model.queries.GetUserByIdQuery;
import com.backendsems.iam.domain.services.UserQueryService;
import com.backendsems.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * UserQueryServiceImpl - Implementación del servicio de consultas para usuarios
 */
@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> handle(GetUserByEmailQuery query) {
        return userRepository.findByEmail(query.email());
    }

    @Override
    public Optional<User> handle(GetUserByIdQuery query) {
        return userRepository.findById(query.userId());
    }

    @Override
    public List<User> handleGetAll() {
        return userRepository.findAll();
    }
}