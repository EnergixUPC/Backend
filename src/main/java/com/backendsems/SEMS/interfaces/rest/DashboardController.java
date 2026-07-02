package com.backendsems.SEMS.interfaces.rest;

import com.backendsems.SEMS.domain.model.queries.GetDashboardByUserIdQuery;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.domain.services.DashboardQueryService;
import com.backendsems.SEMS.interfaces.rest.resources.DashboardResource;
import com.backendsems.SEMS.interfaces.rest.resources.ReceiptValidationResource;
import com.backendsems.SEMS.interfaces.rest.resources.ValidateReceiptResource;
import com.backendsems.SEMS.interfaces.rest.transform.DashboardResourceAssembler;
import com.backendsems.iam.application.internal.outboundservices.tokens.TokenService;
import com.backendsems.profiles.interfaces.acl.ProfilesContextFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Dashboard", description = "Available Dashboard Endpoints")
public class DashboardController {

    // US21: umbral de discrepancia (%) por debajo del cual se considera que el recibo real
    // coincide razonablemente con la factura estimada por la plataforma.
    private static final double RECEIPT_TOLERANCE_PERCENT = 15.0;

    private final DashboardQueryService dashboardQueryService;
    private final TokenService tokenService;
    private final ProfilesContextFacade profilesContextFacade;

    public DashboardController(DashboardQueryService dashboardQueryService,
                               TokenService tokenService,
                               ProfilesContextFacade profilesContextFacade) {
        this.dashboardQueryService = dashboardQueryService;
        this.tokenService = tokenService;
        this.profilesContextFacade = profilesContextFacade;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DashboardResource> getDashboard(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenService.getEmailFromToken(token);
        Long profileId = profilesContextFacade.fetchProfileIdByEmail(email);
        if (profileId == null) return ResponseEntity.badRequest().build();

        var data = dashboardQueryService.handle(new GetDashboardByUserIdQuery(new UserId(profileId)));
        return ResponseEntity.ok(DashboardResourceAssembler.toResource(data));
    }

    /**
     * US21: Validar precisión de datos del EMS.
     * Compara el monto de un recibo eléctrico ingresado manualmente por el usuario contra
     * la factura estimada por la plataforma para el mes en curso (consumo x tarifa referencial).
     */
    @PostMapping(value = "/validate-receipt", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Validate a real receipt amount against the platform's estimated bill",
            description = "US21: compares a manually entered electricity bill amount with the platform's estimated bill for the current month")
    public ResponseEntity<ReceiptValidationResource> validateReceipt(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ValidateReceiptResource resource) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenService.getEmailFromToken(token);
        Long profileId = profilesContextFacade.fetchProfileIdByEmail(email);
        if (profileId == null) return ResponseEntity.badRequest().build();

        var data = dashboardQueryService.handle(new GetDashboardByUserIdQuery(new UserId(profileId)));
        double estimatedBill = data.estimatedBill();
        double billAmount = resource.billAmount();

        double differenceAmount = Math.abs(billAmount - estimatedBill);
        double differencePercent = estimatedBill > 0 ? (differenceAmount / estimatedBill) * 100.0 : 100.0;
        double matchPercent = Math.max(0.0, 100.0 - differencePercent);
        boolean withinTolerance = differencePercent <= RECEIPT_TOLERANCE_PERCENT;

        String message = withinTolerance
                ? "Tu recibo coincide razonablemente con el consumo registrado por la plataforma."
                : "Detectamos una diferencia; revisa que la tarifa configurada corresponda a tu proveedor eléctrico.";
        String tariffDisclaimer = "Esta comparación usa una tarifa referencial (S/. " + data.pricePerKwh()
                + "/kWh); puede no coincidir exactamente con la tarifa real de tu proveedor.";

        return ResponseEntity.ok(new ReceiptValidationResource(
                billAmount,
                estimatedBill,
                data.pricePerKwh(),
                differenceAmount,
                differencePercent,
                matchPercent,
                withinTolerance,
                message,
                tariffDisclaimer
        ));
    }
}
