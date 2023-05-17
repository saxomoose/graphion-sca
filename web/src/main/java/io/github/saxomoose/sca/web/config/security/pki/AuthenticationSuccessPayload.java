package io.github.saxomoose.sca.web.config.security.pki;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationSuccessPayload {
    @JsonProperty("successURL")
    private final String successURL;

    public AuthenticationSuccessPayload(String successURL) {
        this.successURL = successURL;
    }
}
