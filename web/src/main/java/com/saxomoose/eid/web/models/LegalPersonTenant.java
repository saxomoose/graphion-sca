package com.saxomoose.eid.web.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
        name="legal_person_tenant",
        uniqueConstraints = @UniqueConstraint(name = "legal_person_tenant_unique_constraint", columnNames = { "legal_person_id", "tenant_id"}))
@EntityListeners(AuditingEntityListener.class)
public class LegalPersonTenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "legal_person_id")
    private LegalPerson legalPerson;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
}
