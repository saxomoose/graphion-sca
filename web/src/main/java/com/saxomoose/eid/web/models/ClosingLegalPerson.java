package com.saxomoose.eid.web.models;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
@Table(
        name = "closing_legal_person",
        uniqueConstraints = @UniqueConstraint(name="unique_constraint", columnNames = { "closing_id", "legal_person_id" }))
public class ClosingLegalPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "closing_id")
    private Closing closing;

    @ManyToOne
    @JoinColumn(name = "legal_person_id")
    private LegalPerson legalPerson;

    @OneToMany(mappedBy = "closingLegalPerson")
    private Collection<OfficerSignatory> officerSignatories;

    // Is the legal person validly represented? 2 Requirements.
    @Column(columnDefinition = "boolean default false")
    private Boolean status = Boolean.FALSE;
}
