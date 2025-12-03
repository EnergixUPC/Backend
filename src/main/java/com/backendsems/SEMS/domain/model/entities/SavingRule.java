package com.backendsems.SEMS.domain.model.entities;

import com.backendsems.SEMS.domain.model.aggregates.UserSetting;
import com.backendsems.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * SavingRule
 * Entity que representa una regla de ahorro automático.
 */
@Getter
@Entity
@Table(name = "saving_rules")
public class SavingRule extends AuditableModel {

    @Column(nullable = false)
    @NotNull
    private String name;

    @Column(nullable = false)
    private boolean isEnabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_setting_id", nullable = false)
    private UserSetting userSetting;

    public SavingRule() {
    }

    public SavingRule(String name, boolean isEnabled) {
        this.name = name;
        this.isEnabled = isEnabled;
    }

    public void assignUserSetting(UserSetting userSetting) {
        this.userSetting = userSetting;
    }

    public void update(String name, boolean isEnabled) {
        this.name = name;
        this.isEnabled = isEnabled;
    }
    
    public void toggle() {
        this.isEnabled = !this.isEnabled;
    }
}
