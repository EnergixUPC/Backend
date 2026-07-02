package com.backendsems.SEMS.interfaces.rest.resources;

/**
 * ReceiptValidationResource
 * US21: resultado de comparar el monto de un recibo eléctrico real contra la factura
 * estimada por la plataforma para el mes en curso.
 */
public record ReceiptValidationResource(
        double billAmount,
        double estimatedBill,
        double pricePerKwh,
        double differenceAmount,
        double differencePercent,
        double matchPercent,
        boolean withinTolerance,
        String message,
        String tariffDisclaimer
) {
}
