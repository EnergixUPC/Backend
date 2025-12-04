package com.backendsems.iam.domain.model.aggregates;

import com.backendsems.iam.domain.model.entities.Role;
import com.backendsems.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User Aggregate Root - Agregado raíz para usuarios
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User extends AuditableAbstractAggregateRoot<User> {

    @Column(unique = true)
    private String email;

    private String password;

    private String name;

    private String lastName;

    private String phone;

    private String address;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.roles = new HashSet<>();
    }

    public User(String email, String password, String name, String lastName, String phone, String address) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.roles = new HashSet<>();
    }

    public User(String email, String password, String name, String lastName, String phone, String address, List<Role> roles) {
        this(email, password, name, lastName, phone, address);
        if (roles != null) {
            this.roles.addAll(roles);
        }
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void addRoles(List<Role> roles) {
        if (roles != null) {
            this.roles.addAll(roles);
        }
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(role -> role.getStringName().equals(roleName));
    }

    /**
     * Actualiza el email del usuario.
     * @param newEmail El nuevo email.
     */
    public void updateEmail(String newEmail) {
        this.email = newEmail;
    }
}