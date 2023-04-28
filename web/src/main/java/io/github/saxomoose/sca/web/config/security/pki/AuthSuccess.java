package io.github.saxomoose.sca.web.config.security.pki;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthSuccess {
    @JsonProperty("sub")
    private String sub;

    @JsonProperty("auth")
    private List<String> auth;

    public AuthSuccess(String sub, List<String> auth) {
        this.sub = sub;
        this.auth = auth;
    }
}
