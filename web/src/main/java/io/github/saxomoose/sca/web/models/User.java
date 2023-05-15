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
public class User implements Serializable {
    @Id
    private final Long id;
    private final String nrn;
    private String firstName;
    private String lastName;
    private String username;
    // Art. 1:5 New Belgian Civil Code.
    private Boolean electronicSignatureConsent;

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

    public static User of(
        String firstName,
        String lastName,
        String username,
        Boolean electronicSignatureConsent)
    {
        // User should only be enabled after email verification.
        // Role should be set by seeder.
        // Tenant should be set to seeders' tenant.
        return new User(null, null, firstName, lastName, username, Boolean.FALSE);
    }

    // Only used in seeder.
    public static User of(
        String nrn,
        String firstName,
        String lastName,
        String username,
        Boolean electronicSignatureConsent
    )
    {
        return new User(null, nrn, firstName, lastName, username, Boolean.FALSE);
    }

    // remove public?
    public User(
        Long id,
        String nrn,
        String firstName,
        String lastName,
        String username,
        Boolean electronicSignatureConsent
    )
    {
        this.id = id;
        this.nrn = nrn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.electronicSignatureConsent = electronicSignatureConsent;
    }

    // public Long tenantId()
    // {
    //     return tenant;
    // }

    public Long id()
    {
        return id;
    }

    public String nrn()
    {
        return nrn;
    }

    public String firstName()
    {
        return firstName;
    }

    public String lastName()
    {
        return lastName;
    }

    public String username()
    {
        return username;
    }

    public Boolean electronicSignatureConsent()
    {
        return electronicSignatureConsent;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

    // public List<GrantedAuthority> authorities() {
    //     return List.of(new SimpleGrantedAuthority(role().toString()));
    // }

    public String email()
    {
        return username();
    }
}
