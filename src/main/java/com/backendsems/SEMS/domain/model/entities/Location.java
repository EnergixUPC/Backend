package com.backendsems.SEMS.domain.model.entities;

import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Entity
@Table(name = "locations")
public class Location extends AuditableModel {

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id", nullable = false))
    @NotNull
    private UserId userId;

    @Column(nullable = false)
    @NotNull
    private String name;

    public Location() {
    }

    public Location(UserId userId, String name) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Location name cannot be null or blank");
        }
        this.userId = userId;
        this.name = name;
    }
}
