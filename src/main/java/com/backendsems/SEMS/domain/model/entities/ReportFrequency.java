package com.backendsems.SEMS.domain.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_settings_report_frequency")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportFrequency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_settings_id", nullable = false)
    private Long userSettingsId;

    @Column(name = "frequency", nullable = false)
    private String frequency;
}