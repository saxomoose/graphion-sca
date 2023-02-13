package com.saxomoose.eid.web.models;

import com.saxomoose.eid.web.enums.RoleEnum;
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
@Table(name="users")
public class ApplicationUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private Collection<Closing> closings;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private Collection<Signatory> signatories;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private Collection<Document> documents;

    @NotNull
    @Column(unique = true)
    @Email
    private String username;

    @NotNull
    private String password;

    // Required by UserDetails interface.
    @NotNull
    private boolean enabled;

    @NotNull
    @Column(columnDefinition = "smallint default 0")
    private RoleEnum role = RoleEnum.ADMIN;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
