package com.backendsems.experiments.infrastructure.persistence.jpa.repositories;

import com.backendsems.experiments.domain.model.aggregates.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExperimentRepository extends JpaRepository<Experiment, String> {
}
