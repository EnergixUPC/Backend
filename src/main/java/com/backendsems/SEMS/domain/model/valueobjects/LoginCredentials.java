package com.backendsems.SEMS.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginCredentials {
    @JsonAlias({"email", "username"}) // Acepta tanto "email" como "username" en el JSON
    private String username;
    private String password;
}