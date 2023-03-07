package io.github.saxomoose.sca.web.models;

import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table(schema = "sca")
public class ClosingSignatory {
    protected Long signatory;
    protected Long document;
    private Instant signatureTimestamp;

    public Instant getSignatureTimestamp() { return signatureTimestamp; }

    public void setSignatureTimestamp(Instant timestamp) { this.signatureTimestamp = timestamp; }
}
