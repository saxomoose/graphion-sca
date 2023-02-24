package com.saxomoose.eid.web.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
        name="signatory_tenant",
        uniqueConstraints = @UniqueConstraint(name = "signatory_tenant_unique_constraint", columnNames = { "signatory_id", "tenant_id" }))
@EntityListeners(AuditingEntityListener.class)
public class SignatoryTenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signatory_id")
    private Signatory signatory;
}
