package io.github.saxomoose.sca.web.models;

import java.time.LocalDateTime;

public class ClosingSignatory {
    private final Long closingId;
    private final Long signatoryId;
    private Long documentId;
    private LocalDateTime signatureTimestamp;

    public static ClosingSignatory of(Long signatoryId, Long documentId)
    {
        return new ClosingSignatory(null, signatoryId, documentId, null);
    }
    
    public static ClosingSignatory of(Long closingId, Long signatoryId, Long documentId)
    {
        return new ClosingSignatory(closingId, signatoryId, documentId, null);
    }

    public ClosingSignatory(Long closingId, Long signatoryId, Long documentId, LocalDateTime signatoryTimestamp) 
    {
        this.closingId = closingId;
        this.signatoryId = signatoryId;
        this.documentId = documentId;
        this.signatureTimestamp = signatoryTimestamp;
    }

    public Long closingId() 
    {
        return closingId;
    }

    public Long signatoryId() 
    {
        return signatoryId;
    }

    public Long documentId() 
    {
        return documentId;
    }

    public LocalDateTime signatureTimestamp() 
    {
        return signatureTimestamp;
    }
}
