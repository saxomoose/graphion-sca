package com.saxomoose.eid.web.models;

import com.saxomoose.eid.web.enums.FunctionEnum;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
        name = "legal_person_signatory",
        indexes = @Index(columnList = "legal_person_id, signatory_id, function", unique = true)
)
public class LegalPersonSignatory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "legal_person_id")
    private LegalPerson legalPerson;

    @ManyToOne
    @JoinColumn(name = "signatory_id")
    private Signatory signatory;

    @NotNull
    private FunctionEnum function;

    @Nullable
    private Long enterpriseNumber;
}
