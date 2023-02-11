package com.saxomoose.eid.web.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "closing_signatory")
public class ClosingSignatory {
    @EmbeddedId
    private ClosingSignatoryKey id;

    @ManyToOne
    @MapsId("closingId")
    @JoinColumn(name = "closing_id")
    private Closing closing;

    @ManyToOne
    @MapsId("signatoryId")
    @JoinColumn(name = "signatory_id")
    private Signatory signatory;

    @ManyToOne
    @MapsId("documentId")
    @JoinColumn(name = "document_id")
    private Document document;

    @Nullable
    private Instant signatureTimestamp;

    public ClosingSignatoryKey getId() {
        return id;
    }

    public void setId(ClosingSignatoryKey id) {
        this.id = id;
    }

    public Closing getClosing() {
        return closing;
    }

    public void setClosing(Closing closing) {
        this.closing = closing;
    }

    public Signatory getSignatory() {
        return signatory;
    }

    public void setSignatory(Signatory signatory) {
        this.signatory = signatory;
    }

    public Document getDocument() { return document; }

    public void setDocument(Document document) { this.document = document; }

    public Instant getSignatureTimestamp() { return signatureTimestamp; }

    public void setSignatureTimestamp(Instant timestamp) { this.signatureTimestamp = timestamp; }
}
