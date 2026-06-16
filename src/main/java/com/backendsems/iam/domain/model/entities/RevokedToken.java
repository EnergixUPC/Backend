package com.backendsems.iam.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "revoked_tokens")
public class RevokedToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 1000)
    private String token;

    public RevokedToken() {
    }

    public RevokedToken(String token) {
        this.token = token;
    }
}
