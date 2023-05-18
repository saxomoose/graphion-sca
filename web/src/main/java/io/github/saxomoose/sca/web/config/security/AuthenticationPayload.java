package io.github.saxomoose.sca.web.config.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.webeid.security.authtoken.WebEidAuthToken;

public class AuthenticationPayload {
    private WebEidAuthToken token;
    private String username;

    @JsonProperty("auth-token")
    public WebEidAuthToken token() 
    {
        return token;
    }

    @JsonProperty("username")
    public String username()
    {
        return username;
    }

    public void setToken(WebEidAuthToken token) 
    {
        this.token = token;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }
}
