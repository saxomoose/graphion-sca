package io.github.saxomoose.sca.web.models;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Table(schema = "sca")
public class Signatory {
    @Id
    private final Long id;

    private final Long nationalRegistrationNumber;
    private final String firstName;
    private final String lastName;
    private final String email;
    // Art. 1:5 New Belgian Civil Code.
    private final Boolean consent;

    private final Long tenant;

    @Version
    private Long version;
    @CreatedBy
    private Long createdBy;
    @LastModifiedBy
    private Long lastModifiedBy;
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Signatory signatory = (Signatory) o;
        return Objects.equals(id, signatory.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
