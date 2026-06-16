package com.backendsems.SEMS.domain.model.queries;

import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import java.time.LocalDate;

public record CompareConsumptionQuery(
    UserId userId,
    LocalDate period1Start,
    LocalDate period1End,
    LocalDate period2Start,
    LocalDate period2End
) {
}
