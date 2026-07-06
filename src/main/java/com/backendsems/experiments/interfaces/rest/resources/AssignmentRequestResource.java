package com.backendsems.experiments.interfaces.rest.resources;

/**
 * AssignmentRequestResource
 * Cuerpo de la petición para pedir/recuperar la variante de un sujeto en un experimento.
 * {@code deploymentEnv} es opcional: "production" o "test" para forzar la variante de ese
 * entorno de despliegue (ver ExperimentCommandServiceImpl); si se omite, se usa bucketing aleatorio.
 */
public record AssignmentRequestResource(String subjectId, String deploymentEnv) {
}
