package io.github.saxomoose.sca.web.controllers.pki;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Digest {
    private String base64Digest;
    private String digestAlgorithm;

    public Digest() { }
    
    public Digest(String base64Digest, String digestAlgorithm) {
        this.base64Digest = base64Digest;
        this.digestAlgorithm = digestAlgorithm;
    }

    @JsonProperty("hash")
    public String base64Digest()
    {
        return base64Digest;
    }

    @JsonProperty("hashFunction")
    public String digestAlgorithm()
    {
        return digestAlgorithm;
    }

    public void setBase64Digest(String hash)
    {
        this.base64Digest = hash;
    }

    public void setDigestAlgorithm(String hashFunction)
    {
        this.digestAlgorithm = hashFunction;
    }
}
