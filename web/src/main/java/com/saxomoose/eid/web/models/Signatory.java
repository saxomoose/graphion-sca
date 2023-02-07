package com.saxomoose.eid.web.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.Collection;

@Entity
@Table(name="signatories")
public class Signatory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Column(unique = true)
    private int nationRegistrationNumber;

    @OneToMany(mappedBy = "signatory", orphanRemoval = true)
    private Collection<ClosingSignatory> closingSignatories;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNationRegistrationNumber() {
        return nationRegistrationNumber;
    }

    public void setNationRegistrationNumber(int nationRegistrationNumber) {
        this.nationRegistrationNumber = nationRegistrationNumber;
    }

    public Collection<ClosingSignatory> getClosingSignatories() {
        return closingSignatories;
    }

    public void setClosingSignatories(Collection<ClosingSignatory> closingSignatories) {
        this.closingSignatories = closingSignatories;
    }
}
