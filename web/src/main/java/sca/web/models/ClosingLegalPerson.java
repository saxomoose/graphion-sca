package sca.web.models;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.util.Collection;

public class ClosingLegalPerson {
    @Id
    private Long id;

    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "closing_id")
    private Closing closing;

    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "legal_person_id")
    private LegalPerson legalPerson;

//    @OneToMany(mappedBy = "closingLegalPerson")
    private Collection<Officer> officerSignatories;

    // Is the legal person validly represented? 2 Requirements.
//    @Column(columnDefinition = "boolean default false")
    private Boolean status = Boolean.FALSE;
}
