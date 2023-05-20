package io.github.saxomoose.sca.web.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
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
    // @ReadOnlyProperty
    private final UUID uuid;
    private Boolean enabled;
    private String firstName;
    private String lastName;
    private String username;
    private LocalDate dateOfBirth;
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


    public static User of(Long id, String firstName, String lastName)
    {
        return new User(id, null, null, firstName, lastName, null, null, null);
    }

    public static User of(
        String firstName,
        String lastName,
        String username,
        LocalDate dateOfBirth,
        Boolean electronicSignatureConsent)
    {
        // User should only be enabled after email verification.
        // Role should be set by seeder.
        // Tenant should be set to seeders' tenant.
        return new User(null, null, Boolean.FALSE, firstName, lastName, username, dateOfBirth, Boolean.FALSE);
    }

    public User(
        Long id,
        UUID uuid,
        Boolean enabled,
        String firstName,
        String lastName,
        String username,
        LocalDate dateOfBirth,
        Boolean electronicSignatureConsent
    )
    {
        this.id = id;
        this.uuid = uuid;
        this.enabled = enabled;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.dateOfBirth = dateOfBirth;
        this.electronicSignatureConsent = electronicSignatureConsent;
    }

    public Long id()
    {
        return id;
    }

    public UUID uuid()
    {
        return uuid;
    }

    private Boolean enabled()
    {
        return enabled;
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

    public LocalDate dateOfBirth()
    {
        return dateOfBirth;
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
