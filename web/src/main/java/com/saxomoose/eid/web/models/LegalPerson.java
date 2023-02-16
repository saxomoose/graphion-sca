package com.saxomoose.eid.web.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

@Entity
@Table(
        name = "legal_persons",
        uniqueConstraints = { @UniqueConstraint(name = "enterprise_number_unique_constraint", columnNames = { "enterprise_number" })})
public class LegalPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToMany(mappedBy="legalPerson", orphanRemoval = true)
    private Collection<ClosingLegalPerson> closingLegalPersons;

    @NotNull
    @Column(name = "enterprise_number")
    private Long enterpriseNumber;
}
