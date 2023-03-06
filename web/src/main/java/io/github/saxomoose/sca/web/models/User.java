package io.github.saxomoose.sca.web.models;

import io.github.saxomoose.sca.web.enums.Role;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Table(schema = "sca")
public class User implements UserDetails {
    @Id
    private final Long id;
    private final String username;
    private String password;
    // Required by UserDetails interface.
    private Boolean enabled;
    private Role role;

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

    // TODO: remove.
    public static User of(String username, String password) {
        return new User(null, username, password, Boolean.TRUE, Role.ROLE_ADMIN, 1L);
    }

    public static User of(String username, String password, Long tenant) {
        // User should only be enabled after email verification.
        // Role should be set by seeder.
        // Tenant should be set to seeders' tenant.
        return new User(null, username, password, Boolean.TRUE, Role.ROLE_ADMIN, tenant);
    }

    // remove public?
    public User(Long id, String username, String password, Boolean enabled, Role role, Long tenant) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.role = role;
        this.tenant = tenant;
    }

    public Long getId() {
        return id;
    }
    @Override
    public String getUsername() { return username; }
    @Override
    public String getPassword() {
        return password;
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
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authList = new ArrayList<>();
        authList.add(new SimpleGrantedAuthority(this.role.name()));

        return authList;
    }
    public Long getTenant() { return tenant; }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    public void setRole(Role role) { this.role = role; }

}
