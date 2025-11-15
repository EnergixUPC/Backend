package com.backendsems.SEMS.domain.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_settings_report_format")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportFormat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_settings_id", nullable = false)
    private Long userSettingsId;

    @Column(name = "format", nullable = false)
    private String format;
}