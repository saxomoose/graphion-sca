package io.github.saxomoose.sca.web.config.security.pki;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.webeid.security.authtoken.WebEidAuthToken;

public class Authentication {
    private WebEidAuthToken token;
    private String target;

    @JsonProperty("auth-token")
    public WebEidAuthToken token() 
    {
        return token;
    }

    @JsonProperty("target")
    public String target()
    {
        return target;
    }

    public void setToken(WebEidAuthToken token) 
    {
        this.token = token;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }
}
