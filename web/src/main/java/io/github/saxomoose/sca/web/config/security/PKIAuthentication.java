package io.github.saxomoose.sca.web.config.security;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.models.User;

public class PKIAuthentication extends PreAuthenticatedAuthenticationToken {
    private static final Logger logger = LoggerFactory.getLogger(PKIAuthentication.class);

    private final Tenant defaultTenant;

    public static Authentication of(User user)
    {
        return new PKIAuthentication(user, null, null);
    }

    public static Authentication of(User user, List<GrantedAuthority> authorities)
    {
        return new PKIAuthentication(user, null, authorities);
    }
    
    public static Authentication of(User user, Tenant defaultTenant, List<GrantedAuthority> authorities)
    {
        return new PKIAuthentication(user, defaultTenant, authorities);
    }

    private PKIAuthentication(User principal, Tenant defaultTenant, List<GrantedAuthority> authorities) 
    {
        // TODO: user.authorities(). Refresh authorities later on.
        super(
            principal, 
            null, 
            authorities);
        // this.subjectIdCode = subjectIdCode;
        this.defaultTenant = defaultTenant;
    }

    // public UUID subjectIdCode() 
    // {
    //     return subjectIdCode;
    // }

    public Tenant defaultTenant()
    {
        return defaultTenant;
    }

}
