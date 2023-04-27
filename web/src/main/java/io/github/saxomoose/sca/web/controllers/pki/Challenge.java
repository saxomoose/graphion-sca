package io.github.saxomoose.sca.web.controllers.pki;

public class Challenge {
    private String nonce;

    public String nonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
