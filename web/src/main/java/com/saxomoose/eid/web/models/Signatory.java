package com.saxomoose.eid.web.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Collection;

@Entity
@Table(
        name = "signatories",
        uniqueConstraints = {
                @UniqueConstraint(name = "email_unique_constraint", columnNames = { "email" }),
                @UniqueConstraint(name = "national_registration_number_unique_constraint", columnNames = { "national_registration_number" })})
@EntityListeners(AuditingEntityListener.class)
public class Signatory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private OfficerSignatory officerSignatory;

    @OneToMany(mappedBy = "signatory", orphanRemoval = true)
    private Collection<ClosingSignatory> closingSignatories;

    @ManyToMany(mappedBy = "signatories")
    private Collection<User> users;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Column(name = "national_registration_number")
    private Long nationalRegistrationNumber;

    // Art. 1:5 New Belgian Civil Code.
    @NotNull
    private Boolean consent;

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
