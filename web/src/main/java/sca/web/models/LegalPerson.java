package sca.web.models;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.util.Collection;

public class LegalPerson {
    @Id
    private Integer id;

//    @OneToMany(mappedBy="legalPerson")
    private Collection<ClosingLegalPerson> closingLegalPersons;

//    @OneToMany(mappedBy = "legalPerson")
    private Collection<LegalPersonTenant> legalPersonTenants;

    @NotNull
    private Long enterpriseNumber;


}
