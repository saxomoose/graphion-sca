package com.saxomoose.eid.web.models;

import com.saxomoose.eid.web.enums.SupportedMediaType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.Collection;

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
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @NotNull
    @ManyToOne
    @JoinColumn(name="closing_id")
    private Closing closing;

    @OneToMany(mappedBy = "document")
    private Collection<ClosingSignatory> closingSignatories;

    @OneToMany(mappedBy = "document")
    private Collection<Officer> officerSignatories;

    @NotNull
    private String filename;

    @NotNull
    private SupportedMediaType mediaType = SupportedMediaType.PDF;

    // Minio attributes: uuid, versionuuid

    @CreatedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "created_timestamp")
    private Instant createdTimestamp;

    @LastModifiedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "last_modified_timestamp")
    protected Instant lastModifiedTimestamp;

    public Document() { }

    public Document(Tenant tenant, Closing closing, String fileName) {
        this.tenant = tenant;
        this.closing = closing;
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
