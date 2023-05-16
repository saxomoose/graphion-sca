package io.github.saxomoose.sca.web.models;

import org.springframework.data.relational.core.mapping.Table;
import io.github.saxomoose.sca.web.enums.Role;

@Table(schema = "sca")
public class TenantUser {
    protected Long tenant;
    protected Long user;

    private Role role;
    private Boolean defaultConnection;

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
