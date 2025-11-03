package com.backendsems.SEMS.domain.model.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenPair {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String refreshToken;
    
    public TokenPair(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresIn = 86400L; // 24 hours in seconds
    }
}