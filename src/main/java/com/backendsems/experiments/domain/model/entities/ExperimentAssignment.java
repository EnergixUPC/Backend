package com.backendsems.experiments.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * ExperimentAssignment
 * Registra la variante asignada a un sujeto (visitante anónimo o usuario) para un experimento.
 * Es idempotente por (experimentKey, subjectId): una vez asignada, la variante no cambia.
 */
@Getter
@Entity
@Table(name = "experiment_assignments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"experiment_key", "subject_id"}))
public class ExperimentAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "experiment_key", nullable = false)
    private String experimentKey;

    @Column(name = "subject_id", nullable = false)
    private String subjectId;

    @Column(nullable = false)
    private String variant;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    protected ExperimentAssignment() {
        // Constructor vacío para JPA
    }

    public ExperimentAssignment(String experimentKey, String subjectId, String variant) {
        this.experimentKey = experimentKey;
        this.subjectId = subjectId;
        this.variant = variant;
        this.assignedAt = LocalDateTime.now();
    }
}
