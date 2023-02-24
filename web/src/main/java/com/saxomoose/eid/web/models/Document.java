package com.saxomoose.eid.web.models;

import com.saxomoose.eid.web.enums.SupportedMediaType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(
        name = "documents"
//        uniqueConstraints = { @UniqueConstraint(name = "path_unique_constraint", columnNames = { "path" })}
        )
@EntityListeners(AuditingEntityListener.class)
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @NotNull
    private String filename;

    @NotNull
    private String path;

    @NotNull
    private SupportedMediaType mediaType = SupportedMediaType.PDF;

    // Minio attributes: uuid, versionuuid

    @CreatedDate
    @Column(name = "created_timestamp")
    private Instant createdTimestamp;

    @LastModifiedDate
    @Column(name = "last_modified_timestamp")
    protected Instant lastModifiedTimestamp;

    public Document() { }

    public Document(Tenant tenant, String fileName) {
        this.tenant = tenant;
        this.filename = fileName;
    }

    public Long getId() {
        return id;
    }

    public String getFilename() { return filename; }

    public void setFilename(String filename) { this.filename = filename; }

    public Instant getCreatedTimestamp() { return createdTimestamp; }

    public void setCreatedTimestamp(Instant createdTimestamp) { this.createdTimestamp = createdTimestamp; }

    public Instant getLastModifiedTimestamp() { return lastModifiedTimestamp; }

    public void setLastModifiedTimestamp(Instant modifiedTimestamp) { this.lastModifiedTimestamp = modifiedTimestamp; }
}
