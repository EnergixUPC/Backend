package com.backendsems.Profile.domain.model.aggregates;

import com.backendsems.Profile.domain.model.commands.CreateProfileCommand;
import com.backendsems.Profile.domain.model.valueobjects.Address;
import com.backendsems.Profile.domain.model.valueobjects.EmailAddress;
import com.backendsems.Profile.domain.model.valueobjects.PersonName;
import com.backendsems.Profile.domain.model.valueobjects.PhoneNumber;
import com.backendsems.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Profile Aggregate
 */
@Entity
@Table(name = "profiles")
@Getter
@Setter
public class Profile extends AuditableModel {
    @Embedded
    private PersonName name;

    @Embedded
    private PersonName lastName;

    @Embedded
    private EmailAddress email;

    @Column(nullable = false)
    private String password;

    @Embedded
    private PhoneNumber phone;

    @Embedded
    private Address address;

    public Profile() {
    }

    public Profile(PersonName name, PersonName lastName, EmailAddress email, String password, PhoneNumber phone, Address address) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
    }

    public static Profile create(CreateProfileCommand command) {
        return new Profile(
                new PersonName(command.name()),
                new PersonName(command.lastName()),
                new EmailAddress(command.email()),
                command.password(),
                new PhoneNumber(command.phone()),
                new Address(command.address())
        );
    }
}