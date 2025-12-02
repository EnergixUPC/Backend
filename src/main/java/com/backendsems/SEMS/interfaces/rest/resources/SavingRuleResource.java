package com.backendsems.SEMS.interfaces.rest.resources;

public record SavingRuleResource(
    Long id,
    String name,
    boolean isEnabled
) {
}
