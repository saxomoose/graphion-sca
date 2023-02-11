package com.saxomoose.eid.web.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.Collection;

@Entity
@Table(name = "closings")
public class Closing {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Instant date;

    @OneToMany(mappedBy = "closing", orphanRemoval = true)
    private Collection<Document> documents;

    @OneToMany(mappedBy = "closing", orphanRemoval = true)
    private Collection<ClosingSignatory> closingNaturalPersonSignatories;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Instant getDate() { return date; }

    public void setDate(Instant date) { this.date = date; }

    public Collection<ClosingSignatory> getClosingSignatories() {
        return closingNaturalPersonSignatories;
    }

    public void setClosingSignatories(Collection<ClosingSignatory> closingSignatories) {
        this.closingNaturalPersonSignatories = closingSignatories;
    }

    public Collection<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Collection<Document> documents) {
        this.documents = documents;
    }
}
