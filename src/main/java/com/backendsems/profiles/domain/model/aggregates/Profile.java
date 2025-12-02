package com.backendsems.profiles.domain.model.aggregates;

import com.backendsems.profiles.domain.model.valueobjects.Address;
import com.backendsems.profiles.domain.model.valueobjects.EmailAddress;
import com.backendsems.profiles.domain.model.valueobjects.PersonName;
import com.backendsems.profiles.domain.model.valueobjects.PhoneNumber;
import com.backendsems.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

/**
 * Profile Aggregate Root
 * Represents a user profile in the system.
 */
@Getter
@Entity
public class Profile extends AuditableAbstractAggregateRoot<Profile> {
    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "first_name"))
    private PersonName name;

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "last_name"))
    private PersonName lastName;

    @Embedded
    @AttributeOverride(name = "address", column = @Column(name = "email_address"))
    private EmailAddress email;

    @Column(nullable = false)
    private String password;

    @Embedded
    @AttributeOverride(name = "number", column = @Column(name = "phone_number"))
    private PhoneNumber phone;

    @Embedded
    @AttributeOverride(name = "address", column = @Column(name = "profile_address"))
    private Address address;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    public Profile() {
    }

    public Profile(PersonName name, PersonName lastName, EmailAddress email, String password, PhoneNumber phone, Address address, String profilePhotoUrl) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public void update(com.backendsems.profiles.domain.model.commands.UpdateProfileCommand command) {
        this.name = command.name();
        this.lastName = command.lastName();
        this.email = command.email();
        this.phone = command.phone();
        this.address = command.address();
        if (command.profilePhotoUrl() != null) {
            this.profilePhotoUrl = command.profilePhotoUrl();
        }
    }

    public static Profile create(com.backendsems.profiles.domain.model.commands.CreateProfileCommand command) {
        return new Profile(
                new com.backendsems.profiles.domain.model.valueobjects.PersonName(command.name()),
                new com.backendsems.profiles.domain.model.valueobjects.PersonName(command.lastName()),
                new com.backendsems.profiles.domain.model.valueobjects.EmailAddress(command.email()),
                command.password(),
                new com.backendsems.profiles.domain.model.valueobjects.PhoneNumber(command.phone()),
                new com.backendsems.profiles.domain.model.valueobjects.Address(command.address()),
                null // profilePhotoUrl inicial
        );
    }
}