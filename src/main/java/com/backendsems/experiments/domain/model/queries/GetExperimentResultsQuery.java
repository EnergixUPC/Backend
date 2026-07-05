package com.backendsems.experiments.domain.model.queries;

/**
 * GetExperimentResultsQuery
 * Solicita el conteo de asignaciones y eventos por variante de un experimento.
 */
public record GetExperimentResultsQuery(String experimentKey) {
}
