package io.github.saxomoose.sca.web.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class User implements Serializable {
    
    private final Long id;
    private Boolean enabled;
    private String firstName;
    private String lastName;
    private String username;
    private LocalDate dateOfBirth;
    // Art. 1:5 New Belgian Civil Code.
    private Boolean electronicSignatureConsent;

    private Long createdBy;
    private Long lastUpdatedBy;
    private LocalDateTime createdTimestamp;
    private LocalDateTime lastUpdatedTimestamp;

    public static User of(
        String firstName,
        String lastName,
        String username,
        LocalDate dateOfBirth)
    {
        // User should only be enabled after email verification.
        // Role should be set by seeder.
        // Tenant should be set to seeders' tenant.
        return new User(null, null, firstName, lastName, username, dateOfBirth, null);
    }

    public User(
        Long id,
        Boolean enabled,
        String firstName,
        String lastName,
        String username,
        LocalDate dateOfBirth,
        Boolean electronicSignatureConsent
    )
    {
        this.id = id;
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

    public Long createdBy()
    {
        return createdBy;
    }

    public Long lastUpdatedBy()
    {
        return lastUpdatedBy;
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
