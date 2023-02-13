package com.saxomoose.eid.web.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collection;

@Entity
@Table(name = "closings")
@EntityListeners(AuditingEntityListener.class)
public class Closing {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    @OneToMany(mappedBy = "closing", orphanRemoval = true)
    private Collection<Document> documents;

    @OneToMany(mappedBy = "closing", orphanRemoval = true)
    private Collection<ClosingSignatory> closingNaturalPersonSignatories;

    @ManyToMany(targetEntity = Signatory.class)
    @JoinTable(name = "closing_signatory",
            joinColumns = {@JoinColumn(name = "closing_id")},
            inverseJoinColumns = {@JoinColumn(name = "signatory_id")})
    private Collection<Signatory> signatories;

    @NotNull
    private String name;

    @NotNull
    private ZoneOffset zoneOffset;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public ZoneOffset getZoneOffset() { return zoneOffset; }

    public void setZoneOffset(ZoneOffset zoneOffset) { this.zoneOffset = zoneOffset; }

    public LocalDate getDate() { return date; }

    public void setDate(LocalDate date) { this.date = date; }

    public Collection<Signatory> getSignatories() {
        return signatories;
    }
}
