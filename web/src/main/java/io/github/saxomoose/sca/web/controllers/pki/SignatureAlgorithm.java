package io.github.saxomoose.sca.web.controllers.pki;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SignatureAlgorithm {
    private String cryptoAlgorithm;
    private String hashAlgorithm;
    private String paddingAlgorithm;
    
    @JsonProperty("cryptoAlgorithm")
    public String cryptoAlgorithm()
    {
        return cryptoAlgorithm;
    }

    @JsonProperty("hashFunction")
    public String hashAlgorithm()
    {
        return hashAlgorithm;
    }

    @JsonProperty("paddingScheme")
    public String paddingAlgorithm()
    {
        return paddingAlgorithm;
    }

    public void setCryptoAlgorithm(String cryptoAlgorithm)
    {
        this.cryptoAlgorithm = cryptoAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm)
    {
        this.hashAlgorithm = hashAlgorithm;
    }

    public void setPaddingAlgorithm(String paddingAlgorithm)
    {
        this.paddingAlgorithm = paddingAlgorithm;
    }
}
