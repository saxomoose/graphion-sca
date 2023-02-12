package com.saxomoose.eid.web.models;

import com.saxomoose.eid.web.enums.FunctionEnum;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class OfficerSignatory extends Signatory {
    @ManyToOne
    private LegalPerson legalPerson;

    @NotNull
    private FunctionEnum function;

    @Nullable
    private Long enterpriseNumberPermanentRepresentative;
}
