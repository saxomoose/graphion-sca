package com.saxomoose.eid.web.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "closings")
public class Closing {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(columnDefinition = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @OneToMany(mappedBy = "closing", orphanRemoval = true)
    private Collection<Document> documents;

    @OneToMany(mappedBy = "closing", orphanRemoval = true)
    private Collection<ClosingSignatory> closingSignatories;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    public Collection<ClosingSignatory> getClosingSignatories() {
        return closingSignatories;
    }

    public void setClosingSignatories(Collection<ClosingSignatory> closingSignatories) {
        this.closingSignatories = closingSignatories;
    }

    public Collection<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Collection<Document> documents) {
        this.documents = documents;
    }
}
