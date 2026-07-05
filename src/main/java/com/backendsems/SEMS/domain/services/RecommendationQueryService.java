package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.valueobjects.UserId;

import java.util.List;

/**
 * RecommendationQueryService
 * Genera recomendaciones de ahorro para un usuario (US22/Q3).
 */
public interface RecommendationQueryService {

    /**
     * Recomendaciones "legacy": el dispositivo de mayor consumo + 2 consejos genéricos fijos.
     * Se mantiene para /reports/generate y para la variante de control del experimento
     * "personalized-recommendations".
     */
    List<String> generateLegacyRecommendations(UserId userId);

    /**
     * Recomendaciones personalizadas reales: por cada dispositivo top se genera un consejo
     * según su categoría, y se agrega un consejo de horario pico si aplica. Es la variante de
     * tratamiento del experimento "personalized-recommendations".
     */
    List<String> generatePersonalizedRecommendations(UserId userId);
}
