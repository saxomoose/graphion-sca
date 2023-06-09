package io.github.saxomoose.sca.web.models;

import java.time.LocalDateTime;

public class ClosingSignatory {
    private final Long closing;
    private final Long signatory;
    private Long document;
    private LocalDateTime signatureTimestamp; 

    public ClosingSignatory(Long closing, Long signatory, Long document, LocalDateTime signatoryTimestamp) 
    {
        this.closing = closing;
        this.signatory = signatory;
        this.document = document;
        this.signatureTimestamp = signatoryTimestamp;
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

    public LocalDateTime signatureTimestamp() 
    {
        return signatureTimestamp;
    }
}
