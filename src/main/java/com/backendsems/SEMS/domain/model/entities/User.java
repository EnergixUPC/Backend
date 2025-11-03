package com.backendsems.SEMS.domain.model.entities;

import com.backendsems.SEMS.domain.model.aggregates.UserAggregate;
import com.backendsems.SEMS.domain.model.valueobjects.Email;
import com.backendsems.SEMS.domain.model.valueobjects.UserProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * User Entity - Compatibility layer
 * Mantiene compatibilidad con el frontend mientras usa UserAggregate internamente
 */
@Entity
@Table(name = "users_legacy")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @Column(length = 500)
    private String address;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "profile_photo_url", length = 1000)
    private String profilePhotoUrl;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Campos adicionales del frontend SEMS
    @Builder.Default
    @Column(name = "total_consumption")
    private Double totalConsumption = 0.0;
    
    @Builder.Default
    @Column(name = "monthly_savings")
    private Double monthlySavings = 0.0;
    
    @Builder.Default
    @Column(name = "active_devices_count")
    private Integer activeDevicesCount = 0;
    
    @Builder.Default
    @Column(name = "is_premium")
    private Boolean isPremium = false;
    
    public enum Role {
        ADMIN, USER
    }
    
    // Conversion methods to/from UserAggregate
    public static User fromAggregate(UserAggregate aggregate) {
        return User.builder()
                .id(aggregate.getId())
                .username(aggregate.getEmail().getValue())
                .email(aggregate.getEmail().getValue())
                .password(aggregate.getPassword())
                .firstName(aggregate.getProfile().getFirstName())
                .lastName(aggregate.getProfile().getLastName())
                .phoneNumber(aggregate.getProfile().getPhone())
                .role(Role.valueOf(aggregate.getRole().name()))
                .build();
    }
    
    public UserAggregate toAggregate() {
        UserAggregate.Role userRole = UserAggregate.Role.valueOf(this.role.name());
        
        return UserAggregate.builder()
                .id(this.id)
                .email(new Email(this.email))
                .password(this.password)
                .profile(new UserProfile(this.firstName, this.lastName, this.phoneNumber))
                .role(userRole)
                .build();
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
}