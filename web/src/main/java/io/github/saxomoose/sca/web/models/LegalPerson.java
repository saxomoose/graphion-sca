package io.github.saxomoose.sca.web.models;

import io.github.saxomoose.sca.web.enums.Function;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Table(schema = "sca")
public class LegalPerson {
    private final Long tenant;
    @Id
    private final Long id;
    private final Long enterpriseNumber;

    private Set<Officer> officers = new HashSet<>();

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

    public static LegalPerson of(Long tenantId, Long enterpriseNumber) {
        return new LegalPerson(tenantId, null, enterpriseNumber);
    }

    public LegalPerson(Long tenant, Long id, Long enterpriseNumber) {
        this.tenant = tenant;
        this.id = id;
        this.enterpriseNumber = enterpriseNumber;
    }

    public Long tenantId() { return tenant; }
    public Long id() { return id; }
    public Long enterpriseNumber() { return enterpriseNumber; }

    public void setOfficers(Set<Officer> officers) { this.officers = officers; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegalPerson that = (LegalPerson) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addOfficer(Long signatory, Function function, Long enterpriseNumber) {
        this.officers.add(officer(signatory, function, enterpriseNumber));
    }

    private Officer officer(Long signatory, Function function, Long enterpriseNumber) {
        final var officer = new Officer();
        officer.signatory = signatory;
        officer.function = function;
        officer.enterpriseNumber = enterpriseNumber;
        return officer;
    }
}
