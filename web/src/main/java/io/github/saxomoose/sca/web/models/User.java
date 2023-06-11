package io.github.saxomoose.sca.web.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User implements Serializable {
    
    private final Long id;
    private String username;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    // Art. 1:5 New Belgian Civil Code.
    private Boolean electronicSignatureConsent;
    private Boolean enabled;


    private Long createdBy;
    private Long lastUpdatedBy;
    private LocalDateTime createdTimestamp;
    private LocalDateTime lastUpdatedTimestamp;

    private Set<Document> documents;

    public static User withId(Long id, String username, String firstName, String lastName)
    {
        return new User(id, username, firstName, lastName, null , null, null);
    }    
    
    public static User of(
        String username,
        String firstName,
        String lastName,
        LocalDate dateOfBirth)
    {
        // User should only be enabled after email verification.
        // Role should be set by seeder.
        // Tenant should be set to seeders' tenant.
        return new User(null, username, firstName, lastName, dateOfBirth, null, null);
    }

    public User(
        Long id,
        String username,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        Boolean electronicSignatureConsent,
        Boolean enabled
    )
    {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.electronicSignatureConsent = electronicSignatureConsent;
        this.enabled = enabled;
    }

    public Long id()
    {
        return id;
    }

    public String username()
    {
        return username;
    }

    public String firstName()
    {
        return firstName;
    }

    public String lastName()
    {
        return lastName;
    }

    public LocalDate dateOfBirth()
    {
        return dateOfBirth;
    }

    public Boolean electronicSignatureConsent()
    {
        return electronicSignatureConsent;
    }

    private Boolean enabled()
    {
        return enabled;
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

    public Set<Document> documents()
    {
        return documents;
    }

    public void addDocument (Document document)
    {
        if (documents == null) {
            documents = new HashSet<Document>();
        }
        documents.add(document);
    }


}
