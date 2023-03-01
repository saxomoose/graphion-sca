package sca.web.models;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import sca.web.enums.Function;

public class Officer {
    @Id
    private Long id;

    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "closing_legal_person_id")
    private ClosingLegalPerson closingLegalPerson;

//    @OneToOne(optional = false)
//    @JoinColumn(name = "signatory_id")
    private Signatory signatory;

    @NotNull
    private Function function;

    private Long enterpriseNumber;
}
