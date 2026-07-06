package com.backendsems.experiments.domain.model.commands;

/**
 * AssignVariantCommand
 * Solicita (o recupera, si ya existe) la variante asignada a un sujeto dentro de un experimento.
 * {@code deploymentEnv} es opcional ("production" | "test" | null): si coincide con un override
 * configurado (ver application.properties: experiments.force-variant.<deploymentEnv>.<experimentKey>),
 * fuerza esa variante en vez del bucketing aleatorio 50/50 por defecto.
 */
public record AssignVariantCommand(String experimentKey, String subjectId, String deploymentEnv) {
    public AssignVariantCommand {
        if (experimentKey == null || experimentKey.isBlank()) {
            throw new IllegalArgumentException("experimentKey cannot be null or blank");
        }
        if (subjectId == null || subjectId.isBlank()) {
            throw new IllegalArgumentException("subjectId cannot be null or blank");
        }
    }
}
