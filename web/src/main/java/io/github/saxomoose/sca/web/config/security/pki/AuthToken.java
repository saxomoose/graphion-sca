package io.github.saxomoose.sca.web.config.security.pki;

import eu.webeid.security.authtoken.WebEidAuthToken;

public class AuthToken {
    private WebEidAuthToken token;

    public WebEidAuthToken token() {
        return token;
    }

    public void setToken(WebEidAuthToken token) {
        this.token = token;
    }
}
