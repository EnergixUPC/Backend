package com.backendsems.experiments.infrastructure.persistence.jpa.repositories;

import com.backendsems.experiments.domain.model.entities.ExperimentAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExperimentAssignmentRepository extends JpaRepository<ExperimentAssignment, Long> {

    Optional<ExperimentAssignment> findByExperimentKeyAndSubjectId(String experimentKey, String subjectId);

    List<ExperimentAssignment> findByExperimentKey(String experimentKey);
}
