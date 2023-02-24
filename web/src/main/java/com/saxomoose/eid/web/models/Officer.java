package com.saxomoose.eid.web.models;

import com.saxomoose.eid.web.enums.Function;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
        name = "officers",
        uniqueConstraints = @UniqueConstraint(name = "officers_unique_constraint", columnNames = { "closing_legal_person_id", "signatory_id", }))
public class Officer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closing_legal_person_id")
    private ClosingLegalPerson closingLegalPerson;

    @OneToOne(optional = false)
    @JoinColumn(name = "signatory_id")
    private Signatory signatory;

    @NotNull
    private Function function;

    private Long enterpriseNumber;
}
