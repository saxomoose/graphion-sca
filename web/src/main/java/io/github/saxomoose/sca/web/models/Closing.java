package io.github.saxomoose.sca.web.models;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

@Table(schema = "sca")
public class Closing {
    @Id
    private Long id;
    private String name;
    private ZoneId zoneId;
    private LocalDate date;

    private final Long tenant;

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

    private Set<ClosingSignatory> naturalPersonSignatories = new HashSet<>();
    private Set<ClosingLegalPerson> legalPersonSignatories = new HashSet<>();

    public static Closing of(String name, ZoneId zoneId, LocalDate date, Long tenant) {
        return new Closing(null, name, zoneId, date, tenant);
    }

    public Closing(Long id, String name, ZoneId zoneId, LocalDate date, Long tenant) {
        this.id = id;
        this.name = name;
        this.zoneId = zoneId;
        this.date = date;
        this.tenant = tenant;
    }

    public Long getId() {
        return id;
    }
    public String getName() { return name; }
    public ZoneId getZoneId() { return zoneId; }
    public LocalDate getDate() { return date; }
    public Long getTenant() { return tenant; }

    public void setZoneId(ZoneOffset zoneId) { this.zoneId = zoneId; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setNaturalPersonSignatories(Set<ClosingSignatory> naturalPersonSignatories) { this.naturalPersonSignatories = naturalPersonSignatories; }
    public void setLegalPersonSignatories(Set<ClosingLegalPerson> legalPersonSignatories) { this.legalPersonSignatories = legalPersonSignatories; }

    public void addNaturalPersonSignatory(Long signatory, Long document) {
        this.naturalPersonSignatories.add(createNaturalPersonSignatory(signatory, document));
    }

    private ClosingSignatory createNaturalPersonSignatory(Long signatory, Long document) {
        final var closingSignatory = new ClosingSignatory();
        closingSignatory.signatory = signatory;
        closingSignatory.document = document;

        return closingSignatory;
    }

    public void addLegalPersonSignatory(Long legalPerson) {
        this.legalPersonSignatories.add(createLegalPersonSignatory(legalPerson));
    }

    private ClosingLegalPerson createLegalPersonSignatory(Long legalPerson) {
        final var closingLegalPerson = new ClosingLegalPerson();
        closingLegalPerson.legalPerson = legalPerson;

        return closingLegalPerson;
    }
}
