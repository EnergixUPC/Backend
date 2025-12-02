package com.backendsems.SEMS.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

public record CreateSavingRuleResource(
    @NotNull String name,
    boolean isEnabled
) {
}
