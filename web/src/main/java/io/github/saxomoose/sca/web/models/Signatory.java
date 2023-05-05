package io.github.saxomoose.sca.web.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table(schema = "sca")
public class Signatory implements Serializable {
    private final Long tenant;
    @Id
    private final Long id;
    private final Long nationalRegistrationNumber;
    private final String firstName;
    private final String lastName;
    private final String email;
    // Art. 1:5 New Belgian Civil Code.
    private final Boolean consent;

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

    public static Signatory of(
            Long tenantId,
            Long nationalRegistrationNumber,
            String firstName,
            String lastName,
            String email) 
    {
        return new Signatory(
            tenantId, null, nationalRegistrationNumber, firstName, lastName, email, Boolean.FALSE);
    }

    public Signatory(
            Long tenant,
            Long id,
            Long nationalRegistrationNumber,
            String firstName,
            String lastName,
            String email,
            Boolean consent) 
    {
        this.tenant = tenant;
        this.id = id;
        this.nationalRegistrationNumber = nationalRegistrationNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.consent = consent;
    }

    public Long tenantId() 
    {
        return this.tenant;
    }

    public Long id() 
    {
        return id;
    }

    public Long nationalRegistrationNumber() 
    {
        return nationalRegistrationNumber;
    }

    public String firstName() 
    {
        return firstName;
    }

    public String lastName() 
    {
        return lastName;
    }

    public String email() 
    {
        return email;
    }

    public Boolean consent() 
    {
        return consent;
    }

    @Override
    public boolean equals(Object o) 
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Signatory signatory = (Signatory) o;
        return Objects.equals(id, signatory.id);
    }

    @Override
    public int hashCode() 
    {
        return Objects.hash(id);
    }
}
