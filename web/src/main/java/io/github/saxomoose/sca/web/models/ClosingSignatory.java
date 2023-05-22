package io.github.saxomoose.sca.web.models;

import java.time.Instant;

public class ClosingSignatory {
    private final Long closing;
    private final Long signatory;
    private Long document;
    private Instant signatureTimestamp; 

    public ClosingSignatory(Long closing, Long signatory, Long document, Instant signatoryTimestamp) 
    {
        this.closing = closing;
        this.signatory = signatory;
        this.document = document;
    }

    public Long closing() 
    {
        return closing;
    }

    public Long signatory() 
    {
        return signatory;
    }

    public Long document() 
    {
        return document;
    }

    public Instant signatureTimestamp() 
    {
        return signatureTimestamp;
    }
}
