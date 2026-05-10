package com.backendsems.SEMS.domain.model.queries;

import java.time.LocalDateTime;
import java.util.List;

public record GetWeeklyConsumptionByDeviceIdsQuery(
        List<String> deviceIds,
        LocalDateTime weekStart,
        LocalDateTime weekEnd
) {}