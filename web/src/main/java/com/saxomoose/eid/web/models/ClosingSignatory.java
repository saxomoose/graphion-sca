package com.saxomoose.eid.web.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "closing_signatory",
        uniqueConstraints = @UniqueConstraint(name = "cs_unique_constraint", columnNames = { "closing_id", "signatory_id", "document_id" }))
public class ClosingSignatory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "closing_id")
    private Closing closing;

    @ManyToOne
    @JoinColumn(name = "signatory_id")
    private Signatory signatory;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    @Nullable
    private Instant signatureTimestamp;

    public Instant getSignatureTimestamp() { return signatureTimestamp; }

    public void setSignatureTimestamp(Instant timestamp) { this.signatureTimestamp = timestamp; }
}
