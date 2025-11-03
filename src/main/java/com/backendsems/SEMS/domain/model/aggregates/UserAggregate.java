package com.backendsems.SEMS.domain.model.aggregates;

import com.backendsems.SEMS.domain.model.valueobjects.Email;
import com.backendsems.SEMS.domain.model.valueobjects.UserProfile;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User Aggregate Root
 * Gestiona todo lo relacionado con un usuario del sistema SEMS
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserAggregate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Embedded
    private Email email;
    
    @Embedded 
    private UserProfile profile;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DeviceAggregate> devices = new ArrayList<>();
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    public enum Role {
        ADMIN, USER
    }
    
    // Domain Methods
    public void addDevice(DeviceAggregate device) {
        device.setUser(this);
        this.devices.add(device);
    }
    
    public void removeDevice(DeviceAggregate device) {
        this.devices.remove(device);
        device.setUser(null);
    }
    
    public boolean canManageDevice(DeviceAggregate device) {
        return this.devices.contains(device);
    }
    
    public UserId getUserId() {
        return new UserId(this.id);
    }
}