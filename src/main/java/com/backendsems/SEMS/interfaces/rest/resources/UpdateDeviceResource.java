package com.backendsems.SEMS.interfaces.rest.resources;

public record UpdateDeviceResource(
        String status,
        String name,
        String category,
        String location,
        Boolean active
) {
}
