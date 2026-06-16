package com.backendsems.SEMS.application.internal.queryservices;

import com.backendsems.SEMS.domain.model.queries.GetMonthlyConsumptionByUserQuery;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.DeviceConsumptionRepository;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.DeviceRepository;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.PreferencesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class DeviceQueryServiceImplTest {

    @Mock
    private DeviceConsumptionRepository consumptionRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private PreferencesRepository preferencesRepository;

    @InjectMocks
    private DeviceQueryServiceImpl deviceQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleMonthlySummary() {
        UserId userId = new UserId(1L);
        GetMonthlyConsumptionByUserQuery query = new GetMonthlyConsumptionByUserQuery(userId);

        List<Object[]> mockResult = List.of(new Object[]{java.sql.Date.valueOf(LocalDate.now()), 15.5});

        when(consumptionRepository.findDailyConsumptionSumByUserIdAndDateRange(
                eq(userId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(mockResult);

        List<Object[]> result = deviceQueryService.handleMonthlySummary(query);

        assertEquals(1, result.size());
        assertEquals(15.5, result.get(0)[1]);
    }

    @Test
    void testHandleCustomSummary() {
        UserId userId = new UserId(1L);
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now();

        List<Object[]> mockResult = List.of(new Object[]{java.sql.Date.valueOf(LocalDate.now()), 20.0});

        when(consumptionRepository.findDailyConsumptionSumByUserIdAndDateRange(
                userId, start, end))
                .thenReturn(mockResult);

        List<Object[]> result = deviceQueryService.handleCustomSummary(userId, start, end);

        assertEquals(1, result.size());
        assertEquals(20.0, result.get(0)[1]);
    }
}
