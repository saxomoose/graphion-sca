package com.saxomoose.eid.web.models;

import com.saxomoose.eid.web.enums.FunctionEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

@Entity
@Table(name = "legal_persons")
public class LegalPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToMany(mappedBy="legalPerson", orphanRemoval = true)
    private Collection<OfficerSignatory> officers;

    @NotNull
    @Column(unique = true)
    private Long enterpriseNumber;

    @NotNull
    private FunctionEnum function;

    // Is the legal person validly represented? 2 Requirements.
    @Column(columnDefinition = "boolean default false")
    private Boolean status = false;
}
