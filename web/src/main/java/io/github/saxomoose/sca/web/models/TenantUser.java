package io.github.saxomoose.sca.web.models;

import org.springframework.data.relational.core.mapping.Table;
import io.github.saxomoose.sca.web.enums.Role;

@Table(schema = "sca")
public class TenantUser {
    private final Long tenant;
    private final Long user;

    private Role role;
    private Boolean defaultConnection;

    public TenantUser(Long tenant, Long user)
    {
        this.tenant = tenant;
        this.user = user;
    }

    public Long tenant()
    {
        return tenant;
    }

    public Long user()
    {
        return user;
    }

    public Role role() 
    {
        return role;
    }

    public Boolean defaultConnection()
    {
        return defaultConnection;
    }

    public void setRole(Role role) 
    {
        this.role = role;
    }

    public void setDefaultConnection(Boolean defaultConnection)
    {
        this.defaultConnection = defaultConnection;
    }
}
