package io.github.saxomoose.sca.web.models;

import io.github.saxomoose.sca.web.enums.RoleEnum;

public class TenantUser {
    private final Long tenant;
    private final Long user;

    private RoleEnum role;
    private Boolean defaultConnection;

    public TenantUser(Long tenant, Long user, RoleEnum role, Boolean defaultConnection)
    {
        this.tenant = tenant;
        this.user = user;
        this.role = role;
        this.defaultConnection = defaultConnection;
    }

    public Long tenant()
    {
        return tenant;
    }

    public Long user()
    {
        return user;
    }

    public RoleEnum role() 
    {
        return role;
    }

    public Boolean defaultConnection()
    {
        return defaultConnection;
    }
}
