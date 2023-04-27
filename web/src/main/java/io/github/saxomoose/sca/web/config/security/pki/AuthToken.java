package io.github.saxomoose.sca.web.config.security.pki;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.webeid.security.authtoken.WebEidAuthToken;

public class AuthToken {
    private WebEidAuthToken token;

    @JsonProperty("auth-token")
    public WebEidAuthToken token() {
        return token;
    }

    public void setToken(WebEidAuthToken token) {
        this.token = token;
    }
}
