package com.saxomoose.eid.web.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "closing_legal_person_signatory",
        indexes = @Index(columnList = "closing_id, legal_person_signatory_id, document_id", unique = true)
)
public class ClosingLegalPersonSignatory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "closing_id")
    private Closing closing;

    @ManyToOne
    @JoinColumn(name = "legal_person_signatory_id")
    private LegalPersonSignatory legalPersonSignatory;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    @Nullable
    private Instant signatureTimestamp;

    public Instant getSignatureTimestamp() { return signatureTimestamp; }

    public void setSignatureTimestamp(Instant timestamp) { this.signatureTimestamp = timestamp; }
}
