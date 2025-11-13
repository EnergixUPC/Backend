package com.backendsems.shared.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Configuration for the application
 * Enables JPA auditing for automatic timestamp management
 * Physical naming strategy configured in application.properties
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}