package com.backendsems.experiments.interfaces.rest.resources;

/**
 * RecordEventResource
 * Cuerpo de la petición para registrar un evento de negocio (ej. "signup_completed").
 * {@code variant} es opcional: si se omite, se resuelve de la asignación existente del sujeto.
 */
public record RecordEventResource(String subjectId, String eventName, String variant, String metadata) {
}
