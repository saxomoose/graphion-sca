package sca.web.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sca.web.enums.Role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class User implements UserDetails {
    @Id
    private final Long id;
    private final String username;
    private String password;
    // Required by UserDetails interface.
    private Boolean enabled;
    private Role role;

    private final AggregateReference<Tenant, Long> tenant;

    public static User of(String username, String password) {
        // User should only be enabled after email verification.
        // Role should be set by seeder.
        // Tenant should be set to seeders' tenant.
        final AggregateReference<Tenant, Long> tenantReference = AggregateReference.to(1L);
        return new User(null, username, password, Boolean.TRUE, Role.ROLE_ADMIN, tenantReference);
    }

    // remove public?
    public User(Long id, String username, String password, Boolean enabled, Role role, AggregateReference<Tenant, Long> tenant) {
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

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
