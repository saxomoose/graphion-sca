package com.saxomoose.eid.web.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.Collection;

@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String filename;

    @NotNull
    @Column(unique = true)
    private String path;

    @OneToMany(mappedBy = "document", orphanRemoval = true)
    private Collection<ClosingSignatory> closingSignatories;

    @NotNull
    @ManyToOne
    @JoinColumn(name="closing_id")
    private Closing closing;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant modifiedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() { return filename; }

    public void setFilename(String filename) { this.filename = filename; }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Closing getClosing() {
        return closing;
    }

    public void setClosing(Closing closing) {
        this.closing = closing;
    }
}
