package io.github.saxomoose.sca.web.config.security.pki;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationSuccessPayload {
    @JsonProperty("destination")
    private final String destination;

    public AuthenticationSuccessPayload(String destination) {
        this.destination = destination;
    }
}
