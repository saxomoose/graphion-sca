package io.github.saxomoose.sca.web.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(schema = "sca")
public class Tenant {
    @Id
    private final Long id;
    // Enforces bucket naming rules.
    // https://stackoverflow.com/questions/50480924/regex-for-s3-bucket-name
    // @Pattern(regexp = "(?!(^xn--|.+-s3alias$))(?=[a-z0-9][a-z0-9-]{1,61}[a-z0-9])")
    private final String name;
    // Default value is value set in application.yml. If default used, not persited.
    private final String endpoint;
    // Set to Tenant name by default. If default used, not persisted.
    private final String bucket;
    // OIDC parameters to connect to Tenant endpoint.
    @Version
    private Long version;
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public static Tenant of(String name) {
        return new Tenant(null, name, null, null);
    }

    public Tenant(Long id, String name, String endpoint, String bucket) {
        this.id = id;
        this.name = name;
        this.endpoint = endpoint;
        this.bucket = bucket;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEndpoint() { return endpoint; }
    public String getBucket() { return bucket; }
}