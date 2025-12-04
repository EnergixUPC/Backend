package com.backendsems.iam.domain.services;

import com.backendsems.iam.domain.model.aggregates.User;
import com.backendsems.iam.domain.model.queries.GetUserByEmailQuery;
import com.backendsems.iam.domain.model.queries.GetUserByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * UserQueryService - Servicio de dominio para consultas de usuario
 */
public interface UserQueryService {

    Optional<User> handle(GetUserByEmailQuery query);

    Optional<User> handle(GetUserByIdQuery query);

    List<User> handleGetAll();
}