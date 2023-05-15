package io.github.saxomoose.sca.web.controllers.pki;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChallengePayload {
    private String nonce;

    public ChallengePayload() {}

    @JsonProperty("nonce")
    public String nonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
