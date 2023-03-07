package io.github.saxomoose.sca.web.models.projections;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table(schema = "sca", name = "signatory")
public class MutableSignatory {
    @Id
    private final Long id;

    private Long nationalRegistrationNumber;
    private String firstName;
    private String lastName;
    private String email;
    // Art. 1:5 New Belgian Civil Code.
    private Boolean consent;

    private Long tenant;

    @Version
    private Long version;

    public MutableSignatory(Long id, Long nationalRegistrationNumber, String firstName, String lastName, String email, Boolean consent, Long tenant) {
        this.id = id;
        this.nationalRegistrationNumber = nationalRegistrationNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.consent = consent;
        this.tenant = tenant;
    }

    public void setNationalRegistrationNumber(Long nationalRegistrationNumber) { this.nationalRegistrationNumber = nationalRegistrationNumber; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setConsent(Boolean consent) { this.consent = consent; }
    public void setEmail(String email) { this.email = email; }
}
