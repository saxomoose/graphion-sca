package io.github.saxomoose.sca.web.models.transfer.pki;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SignaturePayload {
    private String base64Signature;
    private SignatureAlgorithm signatureAlgorithm;

    @JsonProperty("signature")
    public String base64Signature() 
    {
        return base64Signature;
    }

    @JsonProperty("signatureAlgorithm")
    public SignatureAlgorithm signatureAlgorithm() 
    {
        return signatureAlgorithm;
    }

    public void setBase64Signature(String base64Signature) 
    {
        this.base64Signature = base64Signature;
    }

    public void setSignatureAlgorithm(SignatureAlgorithm signatureAlgorithm) 
    {
        this.signatureAlgorithm = signatureAlgorithm;
    }
}
