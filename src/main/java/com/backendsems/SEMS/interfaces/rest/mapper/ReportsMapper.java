package com.backendsems.SEMS.interfaces.rest.mapper;

import com.backendsems.SEMS.domain.model.entities.WeeklyConsumption;
import com.backendsems.SEMS.domain.model.entities.WeeklyConsumptionData;
import com.backendsems.SEMS.interfaces.rest.dto.ReportsDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ReportsMapper
 * Mapper para convertir entre entidades de reportes y DTOs
 */
@Component
public class ReportsMapper {

    /**
     * Convierte una entidad WeeklyConsumption al formato exacto del db.json
     */
    public ReportsDTO.WeeklyConsumptionResponseDTO toWeeklyConsumptionResponseDTO(WeeklyConsumption entity) {
        if (entity == null) return null;

        List<ReportsDTO.WeeklyConsumptionResponseDTO.DataPointDTO> dataPoints = null;
        if (entity.getDataPoints() != null) {
            dataPoints = entity.getDataPoints().stream()
                    .map(this::toDataPointDTO)
                    .collect(Collectors.toList());
        }

        return ReportsDTO.WeeklyConsumptionResponseDTO.builder()
                .id(entity.getId())
                .week(entity.getWeek())
                .startDate(entity.getStartDate().toString())
                .endDate(entity.getEndDate().toString())
                .dataPoints(dataPoints)
                .totalConsumption(entity.getTotalConsumption())
                .averageConsumption(entity.getAverageConsumption())
                .peakDay(entity.getPeakDay())
                .peakConsumption(entity.getPeakConsumption())
                .weeklyAverage(entity.getWeeklyAverage())
                .build();
    }

    /**
     * Convierte una entidad WeeklyConsumptionData a DataPointDTO
     */
    private ReportsDTO.WeeklyConsumptionResponseDTO.DataPointDTO toDataPointDTO(WeeklyConsumptionData entity) {
        if (entity == null) return null;

        return ReportsDTO.WeeklyConsumptionResponseDTO.DataPointDTO.builder()
                .day(entity.getDay())
                .date(entity.getDate().toString())
                .consumption(entity.getConsumption())
                .efficiency(entity.getEfficiency())
                .trend(entity.getTrend())
                .build();
    }

    /**
     * Convierte una lista de entidades WeeklyConsumption a DTOs
     */
    public List<ReportsDTO.WeeklyConsumptionResponseDTO> toWeeklyConsumptionResponseDTOList(List<WeeklyConsumption> entities) {
        if (entities == null) return null;

        return entities.stream()
                .map(this::toWeeklyConsumptionResponseDTO)
                .collect(Collectors.toList());
    }
}