package io.github.saxomoose.sca.web.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table(schema = "sca")
public class Tenant implements Serializable {
    @Id
    private final Long id;
    // Enforces bucket naming rules.
    // https://stackoverflow.com/questions/50480924/regex-for-s3-bucket-name
    // @Pattern(regexp =
    // "(?!(^xn--|.+-s3alias$))(?=[a-z0-9][a-z0-9-]{1,61}[a-z0-9])")
    private String name;
    private final String bucket;
    // OIDC parameters to connect to Tenant endpoint.
    @Version
    private Long version;
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @Transient
    private Set<TenantUser> tenantUsers = new HashSet<>();

    public static Tenant of(String name)
    {
        return new Tenant(null, name, name);
    }

    public Tenant(Long id, String name, String bucket)
    {
        this.id = id;
        this.name = name;
        this.bucket = bucket;
    }

    public Long id()
    {
        return id;
    }

    public String name()
    {
        return name;
    }

    public String bucket()
    {
        return bucket;
    }

    public void setTenantUsers(Set<TenantUser> tenantUsers)
    {
        this.tenantUsers = tenantUsers;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Tenant tenant = (Tenant) o;
        return Objects.equals(id, tenant.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}
