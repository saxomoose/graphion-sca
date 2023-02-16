package com.saxomoose.eid.web.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Collection;

@Entity
@Table(
        name = "documents",
        uniqueConstraints = { @UniqueConstraint(name = "path_unique_constraint", columnNames = { "path" })})
@EntityListeners(AuditingEntityListener.class)
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name="closing_id")
    private Closing closing;

    @OneToMany(mappedBy = "document", orphanRemoval = true)
    private Collection<ClosingSignatory> closingSignatories;

    @OneToMany(mappedBy = "document", orphanRemoval = true)
    private Collection<OfficerSignatory> officerSignatories;

    @NotNull
    private String filename;

    @NotNull
    private String path;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant modifiedAt;

    public Long getId() {
        return id;
    }

    public String getFilename() { return filename; }

    public void setFilename(String filename) { this.filename = filename; }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Instant getCreatedAt() { return createdAt; }

    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getModifiedAt() { return modifiedAt; }

    public void setModifiedAt(Instant modifiedAt) { this.modifiedAt = modifiedAt; }
}
