package com.backendsems.SEMS.interfaces.rest.resources;

import java.time.Instant;

public record ConsumptionByHourResource(Instant timestamp, double kwh) {}
