package com.backendsems.SEMS.interfaces.rest.transform;

import com.backendsems.SEMS.domain.model.entities.Consumption;
import com.backendsems.SEMS.interfaces.rest.resources.ConsumptionResource;

import java.time.format.DateTimeFormatter;

public class ConsumptionFromEntityAssembler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public static ConsumptionResource toResource(Consumption consumption) {
        return new ConsumptionResource(
            consumption.getId(),
            consumption.getConsumption(),
            consumption.getDeviceId(),
            consumption.getCalculatedAt() != null ? consumption.getCalculatedAt().format(FORMATTER) : null,
            consumption.getStatus().toString()
        );
    }
}

