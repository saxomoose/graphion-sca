package io.github.saxomoose.sca.web.models.transfer.pki;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponsePayload {
    @JsonProperty("redirectUrl")
    private final String redirectUrl;

    public ResponsePayload(String redirectUrl) 
    {
        this.redirectUrl = redirectUrl;
    }
}
