package com.backendsems.experiments.domain.model.commands;

/**
 * AssignVariantCommand
 * Solicita (o recupera, si ya existe) la variante asignada a un sujeto dentro de un experimento.
 */
public record AssignVariantCommand(String experimentKey, String subjectId) {
    public AssignVariantCommand {
        if (experimentKey == null || experimentKey.isBlank()) {
            throw new IllegalArgumentException("experimentKey cannot be null or blank");
        }
        if (subjectId == null || subjectId.isBlank()) {
            throw new IllegalArgumentException("subjectId cannot be null or blank");
        }
    }
}
