package com.backendsems.shared.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Abstract base class for auditable aggregate roots in the DDD context
 * Provides automatic auditing capabilities for creation and modification timestamps
 * @param <T> The type of the aggregate root
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class AuditableAbstractAggregateRoot<T extends AuditableAbstractAggregateRoot<T>> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    protected AuditableAbstractAggregateRoot() {
        // Constructor protected para ser usado por las subclases
    }
    
    /**
     * Método para obtener el ID de la entidad
     * @return el ID único de la entidad
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Método para obtener la fecha de creación
     * @return la fecha y hora de creación
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Método para obtener la fecha de última modificación
     * @return la fecha y hora de última modificación
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Método que determina la igualdad basándose en el ID
     * @param obj el objeto a comparar
     * @return true si son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        AuditableAbstractAggregateRoot<?> that = (AuditableAbstractAggregateRoot<?>) obj;
        return id != null && id.equals(that.id);
    }
    
    /**
     * Método que genera el hash code basándose en el ID
     * @return el hash code de la entidad
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}