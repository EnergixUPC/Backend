package com.backendsems.experiments.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Experiment Aggregate Root
 * Define un experimento A/B con sus variantes y el peso (0-100) de cada una.
 */
@Getter
@Entity
@Table(name = "experiments")
public class Experiment {

    // "key" es palabra reservada en H2/MySQL; se mapea explícitamente para evitar que la
    // creación de la tabla falle en tiempo de arranque.
    @Id
    @Column(name = "experiment_key")
    private String key;

    @Column(name = "variants_csv", nullable = false)
    private String variantsCsv;

    @Column(nullable = false)
    private boolean active;

    protected Experiment() {
        // Constructor vacío para JPA
    }

    public Experiment(String key, String variantsCsv, boolean active) {
        this.key = key;
        this.variantsCsv = variantsCsv;
        this.active = active;
    }

    /** Parsea "A:50,B:50" en variantes con su peso relativo (0-100, deben sumar 100). */
    public List<Variant> parseVariants() {
        return Arrays.stream(variantsCsv.split(","))
                .map(String::trim)
                .filter(part -> !part.isEmpty())
                .map(part -> {
                    String[] pieces = part.split(":");
                    return new Variant(pieces[0].trim(), Integer.parseInt(pieces[1].trim()));
                })
                .toList();
    }

    public record Variant(String name, int weight) {
    }
}
