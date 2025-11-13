package com.backendsems.shared.infrastructure.configuration;

import com.backendsems.shared.infrastructure.persistence.jpa.configuration.strategy.SnakeCaseWithPluralizedTablePhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Configuration for the application
 * Enables JPA auditing and configures physical naming strategy
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    
    /**
     * Configures the physical naming strategy for JPA entities
     * Uses snake_case without pluralization to avoid table duplication
     */
    @Bean
    public SnakeCaseWithPluralizedTablePhysicalNamingStrategy physicalNamingStrategy() {
        return new SnakeCaseWithPluralizedTablePhysicalNamingStrategy();
    }
}