package com.backendsems.experiments.domain.model.commands;

/**
 * RecordExperimentEventCommand
 * Registra un evento de negocio para un sujeto. Si {@code variant} viene vacío, se resuelve
 * automáticamente a partir de la asignación existente del sujeto (si la hay).
 */
public record RecordExperimentEventCommand(
        String experimentKey,
        String subjectId,
        String eventName,
        String variant,
        String metadata
) {
    public RecordExperimentEventCommand {
        if (experimentKey == null || experimentKey.isBlank()) {
            throw new IllegalArgumentException("experimentKey cannot be null or blank");
        }
        if (subjectId == null || subjectId.isBlank()) {
            throw new IllegalArgumentException("subjectId cannot be null or blank");
        }
        if (eventName == null || eventName.isBlank()) {
            throw new IllegalArgumentException("eventName cannot be null or blank");
        }
    }
}
