package com.saxomoose.eid.web.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

@Entity
@Table(name = "tenants")
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "tenant")
    private Collection<User> users;

    @OneToMany(mappedBy = "tenant")
    private Collection<Closing> closings;

    @OneToMany(mappedBy = "tenant")
    private Collection<SignatoryTenant> signatoryTenants;

    @OneToMany(mappedBy = "tenant")
    private Collection<LegalPersonTenant> legalPersonTenants;

    @OneToMany(mappedBy = "tenant")
    private Collection<Document> documents;

    @NotNull
    @Column(unique = true)
    // Enforces bucket naming rules.
    // https://stackoverflow.com/questions/50480924/regex-for-s3-bucket-name
//    @Pattern(regexp = "(?!(^xn--|.+-s3alias$))(?=[a-z0-9][a-z0-9-]{1,61}[a-z0-9])")
    private String name;

    // Default value is value set in application.yml. If default used, not persited.
    private String endpoint;

    // Set to Tenant name by default. If default used, not persisted.
    private String bucket;

    // OIDC parameters to connect to Tenant endpoint.

    public Tenant() { }

    public Tenant(String name) {
        this.name = name;
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEndpoint() { return endpoint; }

    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getBucket() { return bucket; }

    public void setBucket(String bucket) { this.bucket = bucket; }
}
