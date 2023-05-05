package io.github.saxomoose.sca.web.models;

import java.time.Instant;

import org.springframework.data.relational.core.mapping.Table;

@Table(schema = "sca")
public class ClosingSignatory {
    protected Long signatory;
    protected Long document;
    private Instant signatureTimestamp;

    public Instant signatureTimestamp()
    {
        return signatureTimestamp;
    }

    public void setSignatureTimestamp(Instant timestamp)
    {
        this.signatureTimestamp = timestamp;
    }
}
