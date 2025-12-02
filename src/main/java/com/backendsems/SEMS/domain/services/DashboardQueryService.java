package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.queries.GetDashboardByUserIdQuery;

public interface DashboardQueryService {
    GetDashboardByUserIdQuery.DashboardData handle(GetDashboardByUserIdQuery query);
}
