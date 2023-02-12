package com.saxomoose.eid.web.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

@Entity
@Table(name = "signatories")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Signatory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "signatory", orphanRemoval = true)
    private Collection<ClosingSignatory> closingSignatories;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @Column(unique = true)
    @Email
    private String email;

    @NotNull
    @Column(unique = true)
    private Long nationalRegistrationNumber;

    // Art. 1:5 New Belgian Civil Code.
    @NotNull
    private Boolean consent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public Long getNationalRegistrationNumber() {
        return nationalRegistrationNumber;
    }

    public void setNationalRegistrationNumber(Long nationalRegistrationNumber) {
        this.nationalRegistrationNumber = nationalRegistrationNumber;
    }

    public Boolean getConsent() { return consent; }

    public void setConsent(Boolean consent) { this.consent = consent; }
}
