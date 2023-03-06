package io.github.saxomoose.sca.web.models;

import io.github.saxomoose.sca.web.enums.Function;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;

@Table(schema = "sca")
public class LegalPerson {
    @Id
    private final Long id;
    private final Long enterpriseNumber;

    private final Long tenant;

    private Set<Officer> officers = new HashSet<>();

    @Version
    private Long version;

    public static LegalPerson of(Long enterpriseNumber, Long tenant) {
        return new LegalPerson(null, enterpriseNumber, tenant);
    }

    public LegalPerson(Long id, Long enterpriseNumber,Long tenant) {
        this.id = id;
        this.enterpriseNumber = enterpriseNumber;
        this.tenant = tenant;
    }

    public Long getId() { return id; }
    public Long getEnterpriseNumber() { return enterpriseNumber; }
    public Long getTenant() { return tenant; }

    public void setOfficers(Set<Officer> officers) { this.officers = officers; }

    public void addOfficer(Long signatory, Function function, Long enterpriseNumber) {
        this.officers.add(createOfficerRef(signatory, function, enterpriseNumber));
    }

    private Officer createOfficerRef(Long signatory, Function function, Long enterpriseNumber) {
        final var officer = new Officer();
        officer.signatory = signatory;
        officer.function = function;
        officer.enterpriseNumber = enterpriseNumber;

        return officer;
    }

}
