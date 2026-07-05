package com.backendsems.experiments.domain.services;

import com.backendsems.experiments.domain.model.queries.GetExperimentResultsQuery;

import java.util.Map;

public interface ExperimentQueryService {
    Map<String, Object> handle(GetExperimentResultsQuery query);
}
