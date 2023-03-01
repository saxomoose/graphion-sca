package sca.web.models;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;

public class SignatoryTenant {
    @Id
    private Long id;

    @NotNull
    @CreatedBy
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "signatory_id")
    private Signatory signatory;
}
