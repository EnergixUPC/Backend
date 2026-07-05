package com.backendsems.experiments.infrastructure.persistence.jpa.repositories;

import com.backendsems.experiments.domain.model.entities.ExperimentEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperimentEventRepository extends JpaRepository<ExperimentEvent, Long> {

    List<ExperimentEvent> findByExperimentKey(String experimentKey);
}
