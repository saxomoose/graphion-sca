package sca.web.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.util.Collection;

public class Signatory {
    @Id
    private Long id;

//    @OneToOne(mappedBy = "signatory")
    private Officer officer;

//    @OneToMany(mappedBy = "signatory")
    private Collection<ClosingSignatory> closingSignatories;

//    @OneToMany(mappedBy = "signatory")
    private Collection<SignatoryTenant> tenantSignatories;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotNull
    private Long nationalRegistrationNumber;

    // Art. 1:5 New Belgian Civil Code.
    @NotNull
    private Boolean consent;

    public Signatory(String firstName, String lastName, String email, Long nationalRegistrationNumber, Boolean consent) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.nationalRegistrationNumber = nationalRegistrationNumber;
        this.consent = consent;
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
