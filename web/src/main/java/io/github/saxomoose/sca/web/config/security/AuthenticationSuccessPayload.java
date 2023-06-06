package io.github.saxomoose.sca.web.config.security;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationSuccessPayload {
    @JsonProperty("successURL")
    private final String successURL;

    public AuthenticationSuccessPayload(String successURL) 
    {
        this.successURL = successURL;
    }
}
