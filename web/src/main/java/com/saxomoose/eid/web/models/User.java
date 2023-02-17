package com.saxomoose.eid.web.models;

import com.saxomoose.eid.web.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(
        name="users",
        uniqueConstraints = { @UniqueConstraint(name = "username_unique_constraint", columnNames = "username") })
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private Collection<Closing> closings;

    @ManyToMany
    @JoinTable(
            name = "signatory_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "signatory_id"))
    private Collection<Signatory> signatories;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private Collection<Document> documents;

    @NotNull
    @Email
    private String username;

    @NotNull
    private String password;

    // Required by UserDetails interface.
    // TODO email validation
    @NotNull
    @Column(columnDefinition = "boolean default true")
    private boolean enabled = Boolean.TRUE;

    @NotNull
    @Column(columnDefinition = "smallint default 0")
    private Role role = Role.ADMIN;

    public User() { }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authList = new ArrayList<>();
        authList.add(new SimpleGrantedAuthority(this.role.name()));
        return authList;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
