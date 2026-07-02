package com.backendsems.SEMS.interfaces.rest.resources;

/**
 * ValidateReceiptResource
 * US21: monto del recibo eléctrico real, ingresado manualmente por el usuario, para
 * compararlo contra la factura estimada por la plataforma.
 */
public record ValidateReceiptResource(
        double billAmount
) {
}
