package com.backendsems.SEMS.domain.model.aggregates;

import com.backendsems.SEMS.domain.model.commands.CreateUserCommand;
import com.backendsems.SEMS.domain.model.valueobjects.Email;
import com.backendsems.SEMS.domain.model.valueobjects.UserProfile;
import com.backendsems.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

/**
 * User Aggregate Root
 * Representa un usuario en el sistema SEMS siguiendo principios DDD
 * Extiende AuditableAbstractAggregateRoot para capacidades de auditoría
 */
// @Entity  // TEMPORALMENTE DESHABILITADO PARA EVITAR DUPLICACIÓN DE TABLAS
@Getter
public class UserAggregate extends AuditableAbstractAggregateRoot<UserAggregate> {

    @Embedded
    private Email email;

    @Embedded
    private UserProfile profile;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public enum Role {
        ADMIN, USER
    }

    /**
     * Constructor por defecto requerido por JPA
     */
    public UserAggregate() {
        super();
        this.role = Role.USER;
    }

    public UserAggregate(CreateUserCommand command) {
        this();
        this.email = new Email(command.email());
        this.profile = new UserProfile(command.firstName(), command.lastName());
        this.password = command.password();
        this.role = Role.USER;
    }

    public UserAggregate(String email, String firstName, String lastName, String password) {
        this();
        this.email = new Email(email);
        this.profile = new UserProfile(firstName, lastName);
        this.password = password;
        this.role = Role.USER;
    }

    // Domain Methods
    public boolean canManageDevice(Long deviceId) {
        return this.role == Role.ADMIN;
    }

    public void updateProfile(String firstName, String lastName) {
        this.profile = new UserProfile(firstName, lastName);
    }

    public void promoteToAdmin() {
        this.role = Role.ADMIN;
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public String getEmailAddress() {
        return this.email != null ? this.email.getValue() : null;
    }

    public String getFullName() {
        return this.profile != null ? this.profile.getFullName() : null;
    }
}