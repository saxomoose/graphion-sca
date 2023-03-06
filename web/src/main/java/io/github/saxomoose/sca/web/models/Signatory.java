package io.github.saxomoose.sca.web.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table(schema = "sca")
public class Signatory {
    @Id
    private final Long id;

//    @OneToOne(mappedBy = "signatory")
//    private Officer officer;

    private final Long nationalRegistrationNumber;
    private final String firstName;
    private final String lastName;
    private String email;
    // Art. 1:5 New Belgian Civil Code.
    private Boolean consent;

    private final Long tenant;

    @Version
    private Long version;

    public static Signatory of(Long nationalRegistrationNumber, String firstName, String lastName, String email, Long tenant) {
        return new Signatory(null, nationalRegistrationNumber, firstName, lastName, email, Boolean.FALSE, tenant);
    }

    public Signatory(Long id, Long nationalRegistrationNumber, String firstName, String lastName, String email, Boolean consent, Long tenant) {
        this.id = id;
        this.nationalRegistrationNumber = nationalRegistrationNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.consent = consent;
        this.tenant = tenant;
    }

    public Long getId() { return id; }
    public Long getNationalRegistrationNumber() {
        return nationalRegistrationNumber;
    }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public Boolean getConsent() { return consent; }
    public Long getTenant() { return this.tenant; }

    public void setConsent(Boolean consent) { this.consent = consent; }
    public void setEmail(String email) { this.email = email; }
}
