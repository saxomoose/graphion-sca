package com.saxomoose.eid.web.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@Table(
        name = "closing_signatory",
        uniqueConstraints = @UniqueConstraint(name = "closing_signatory_unique_constraint", columnNames = { "closing_id", "signatory_id", "document_id" }))
public class ClosingSignatory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "closing_id")
    private Closing closing;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "signatory_id")
    private Signatory signatory;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant signatureTimestamp;

    public Instant getSignatureTimestamp() { return signatureTimestamp; }

    public void setSignatureTimestamp(Instant timestamp) { this.signatureTimestamp = timestamp; }
}
