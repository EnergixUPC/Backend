package com.backendsems.SEMS.interfaces.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDeviceDto {
    private String name;
    private String category;
    private String type;
    private String location;
    private String brand;
    private String model;
    private String status;
}