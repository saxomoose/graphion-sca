package com.saxomoose.eid.web.models;

import com.saxomoose.eid.web.enums.Function;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(
        name = "officer_signatories",
        uniqueConstraints = @UniqueConstraint(name = "unique_constraint", columnNames = { "closing_legal_person_id", "signatory_id", "document_id" }))
public class OfficerSignatory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "closing_legal_person_id")
    private ClosingLegalPerson closingLegalPerson;

    // By default optional = true.
    @OneToOne
    @JoinColumn(name = "signatory_id")
    private Signatory signatory;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    @NotNull
    private Function function;

    @Nullable
    private Long enterpriseNumber;

    @Nullable
    private Instant signatureTimestamp;

    public Instant getSignatureTimestamp() { return signatureTimestamp; }

    public void setSignatureTimestamp(Instant timestamp) { this.signatureTimestamp = timestamp; }
}
