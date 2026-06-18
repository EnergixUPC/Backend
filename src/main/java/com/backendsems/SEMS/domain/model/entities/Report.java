package com.backendsems.SEMS.domain.model.entities;

import com.backendsems.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "reports")
public class Report extends AuditableModel {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String format;

    @Column(nullable = false)
    private String period;

    @Column(nullable = false)
    private String status;

    private String email;

    @Column(name = "download_url")
    private String downloadUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    private String title;

    private String description;

    @Column(name = "generated_by")
    private String generatedBy;

    private String language = "es";

    public Report(Long userId, String type, String format, String period, String status, String name, String description, String generatedBy, String language) {
        this.userId = userId;
        this.type = type;
        this.format = format;
        this.period = period;
        this.status = status;
        this.fileName = name + "." + format;
        this.title = name;
        this.description = description;
        this.generatedBy = generatedBy;
        this.language = language != null ? language : "es";
        this.fileSize = 1024L; // Default simulated size
    }
}
