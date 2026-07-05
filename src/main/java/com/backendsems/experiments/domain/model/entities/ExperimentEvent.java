package com.backendsems.experiments.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * ExperimentEvent
 * Registra un evento de negocio (ej. "signup_completed") asociado a un sujeto y, si ya fue
 * asignado, a la variante del experimento que le corresponde. Es la fuente de datos para
 * calcular las métricas de negocio de la sección 8.2.2 (tasa de registro, reducción de consumo, etc.).
 */
@Getter
@Entity
@Table(name = "experiment_events")
public class ExperimentEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "experiment_key", nullable = false)
    private String experimentKey;

    @Column(name = "subject_id", nullable = false)
    private String subjectId;

    /** Puede ser null si el evento no pertenece a un experimento con variantes (ej. demo-conversion). */
    @Column
    private String variant;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(length = 2000)
    private String metadata;

    protected ExperimentEvent() {
        // Constructor vacío para JPA
    }

    public ExperimentEvent(String experimentKey, String subjectId, String variant, String eventName, String metadata) {
        this.experimentKey = experimentKey;
        this.subjectId = subjectId;
        this.variant = variant;
        this.eventName = eventName;
        this.metadata = metadata;
        this.occurredAt = LocalDateTime.now();
    }
}
