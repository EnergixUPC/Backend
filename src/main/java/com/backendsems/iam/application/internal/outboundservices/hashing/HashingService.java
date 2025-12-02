package com.backendsems.iam.application.internal.outboundservices.hashing;

/**
 * HashingService - Servicio para hashing de contraseñas
 */
public interface HashingService {

    String encode(CharSequence rawPassword);

    boolean matches(CharSequence rawPassword, String encodedPassword);
}