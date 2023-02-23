package com.saxomoose.eid.web.models;

import com.saxomoose.eid.web.enums.Function;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(
        name = "officers",
        uniqueConstraints = @UniqueConstraint(name = "officers_unique_constraint", columnNames = { "closing_legal_person_id", "signatory_id", "document_id" }))
public class Officer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "closing_legal_person_id")
    private ClosingLegalPerson closingLegalPerson;

    // By default optional = true.
    @OneToOne
    @JoinColumn(name = "signatory_id")
    private Signatory signatory;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    @NotNull
    private Function function;

    private Long enterpriseNumber;

    private Instant signatureTimestamp;

    public Instant getSignatureTimestamp() { return signatureTimestamp; }

    public void setSignatureTimestamp(Instant timestamp) { this.signatureTimestamp = timestamp; }
}
