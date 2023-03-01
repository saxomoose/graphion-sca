package sca.web.models;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;

public class LegalPersonTenant {
    @Id
    private Long id;

    @NotNull
    @CreatedBy
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "legal_person_id")
    private LegalPerson legalPerson;

    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
}
