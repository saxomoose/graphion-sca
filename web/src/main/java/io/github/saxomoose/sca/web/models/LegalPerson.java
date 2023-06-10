package io.github.saxomoose.sca.web.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class LegalPerson {
    private final Long tenantId;
    
    private final Long id;
    private final Long enterpriseNumber;

    
    private Long createdBy;
    private Long lastUpdatedBy;
    private LocalDateTime createdTimestamp;
    private LocalDateTime lastUpdatedTimestamp;

    public static LegalPerson of(Long tenantId, Long enterpriseNumber) {
        return new LegalPerson(tenantId, null, enterpriseNumber);
    }

    public LegalPerson(Long tenantId, Long id, Long enterpriseNumber) {
        this.tenantId = tenantId;
        this.id = id;
        this.enterpriseNumber = enterpriseNumber;
    }

    public Long tenantId() {
        return tenantId;
    }

    public Long id() {
        return id;
    }

    public Long enterpriseNumber() {
        return enterpriseNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LegalPerson that = (LegalPerson) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
