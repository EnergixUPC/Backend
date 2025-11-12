package com.backendsems.SEMS.application.queryhandlers;

import com.backendsems.SEMS.domain.model.entities.User;
import com.backendsems.SEMS.domain.model.queries.GetUserByIdQuery;
import com.backendsems.SEMS.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * GetUserByIdQueryHandler
 * Maneja la consulta de usuario por ID
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserByIdQueryHandler {
    
    private final UserRepository userRepository;
    
    public Optional<User> handle(GetUserByIdQuery query) {
        query.validate();
        return userRepository.findById(query.getUserId());
    }
}