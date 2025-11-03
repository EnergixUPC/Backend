package com.backendsems.SEMS.infrastructure.repositories;

import com.backendsems.SEMS.domain.model.aggregates.UserAggregate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository
 * Repositorio para el agregado de Usuario
 */
@Repository
public interface UserRepository extends JpaRepository<UserAggregate, Long> {
    
    @Query("SELECT u FROM UserAggregate u WHERE u.email.value = :email")
    Optional<UserAggregate> findByEmail(@Param("email") String email);
    
    @Query("SELECT u FROM UserAggregate u WHERE u.email.value = :username")
    Optional<UserAggregate> findByUsername(@Param("username") String username);
    
    @Query("SELECT COUNT(u) > 0 FROM UserAggregate u WHERE u.email.value = :email")
    boolean existsByEmail(@Param("email") String email);
    
    @Query("SELECT COUNT(u) > 0 FROM UserAggregate u WHERE u.email.value = :username")
    boolean existsByUsername(@Param("username") String username);
    
    @Query("SELECT u FROM UserAggregate u JOIN FETCH u.devices WHERE u.id = :userId")
    Optional<UserAggregate> findByIdWithDevices(@Param("userId") Long userId);
}