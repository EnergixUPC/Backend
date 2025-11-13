package com.backendsems.SEMS.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginCredentials {
    @JsonProperty("username") // Acepta "username" del JSON
    private String username;
    private String password;
    
    // También acepta "email" como alternativa
    @JsonProperty("email")
    public void setEmail(String email) {
        this.username = email;
    }
}