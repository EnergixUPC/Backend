package com.backendsems.Profile.domain.model.aggregates;

import com.backendsems.Profile.domain.model.valueobjects.EmailAddress;
import com.backendsems.Profile.domain.model.valueobjects.PersonName;
import com.backendsems.Profile.domain.model.valueobjects.StreetAddress;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Profile Aggregate - Agregado raíz para el perfil de usuario
 */
@Entity
@Table(name = "profile")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId; // Relación con User en iam

    @Embedded
    private PersonName personName;

    @Embedded
    private EmailAddress emailAddress;

    @Embedded
    private StreetAddress streetAddress;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 1000)
    private String profilePhotoUrl;

    // Timestamps
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}