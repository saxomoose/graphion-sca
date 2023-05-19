package io.github.saxomoose.sca.web.models;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.jdbc.core.RowMapper;

@Table(schema = "sca")
public class Tenant implements RowMapper<Tenant>, Serializable {
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
    @CreatedBy
    private Long createdBy;
    @LastModifiedBy
    private Long lastModifiedBy;
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

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

    @Override
    public Tenant mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Tenant(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("bucket")
        );
    }
}
