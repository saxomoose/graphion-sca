package com.saxomoose.eid.web.models;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;

@Entity
@Table(name = "closings")
public class Closing {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Value("false")
    private Boolean status;

    @OneToMany(mappedBy = "closing", orphanRemoval = true)
    private Collection<ClosingSignatory> closingSignatories;

    @OneToMany(mappedBy = "closing", orphanRemoval = true)
    private Collection<Document> documents;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

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
