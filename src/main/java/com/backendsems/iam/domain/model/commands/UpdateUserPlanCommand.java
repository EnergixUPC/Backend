package com.backendsems.iam.domain.model.commands;

public record UpdateUserPlanCommand(Long userId, String plan) {
}
