package io.github.saxomoose.sca.web.models;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table(schema = "sca")
public class Closing {
    private final Long tenant;
    @Id
    private final Long id;
    private final String name;
    private final ZoneId zoneId;
    private final LocalDateTime timestamp;

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

    public static Closing of(Long tenantId, String name, ZoneId zoneId, LocalDateTime timestamp)
    {
        return new Closing(tenantId, null, name, zoneId, timestamp);
    }

    public Closing(Long tenant, Long id, String name, ZoneId zoneId, LocalDateTime timestamp)
    {
        this.tenant = tenant;
        this.id = id;
        this.name = name;
        this.zoneId = zoneId;
        this.timestamp = timestamp;
    }

    public Long tenantId()
    {
        return tenant;
    }

    public Long id()
    {
        return id;
    }

    public String name()
    {
        return name;
    }

    public ZoneId zoneId()
    {
        return zoneId;
    }

    public LocalDateTime timestamp()
    {
        return timestamp;
    }

    public ZonedDateTime zonedTimestamp()
    {
        return ZonedDateTime.of(timestamp(), zoneId());
    }

    public void setNaturalPersonSignatories(Set<ClosingSignatory> naturalPersonSignatories)
    {
        this.naturalPersonSignatories = naturalPersonSignatories;
    }

    public void setLegalPersonSignatories(Set<ClosingLegalPerson> legalPersonSignatories)
    {
        this.legalPersonSignatories = legalPersonSignatories;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Closing closing = (Closing) o;
        return Objects.equals(id, closing.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

    public void addNaturalPersonSignatory(Long signatory, Long document)
    {
        this.naturalPersonSignatories.add(naturalPersonSignatory(signatory, document));
    }

    private ClosingSignatory naturalPersonSignatory(Long signatory, Long document)
    {
        final var closingSignatory = new ClosingSignatory();
        closingSignatory.signatory = signatory;
        closingSignatory.document = document;
        return closingSignatory;
    }

    public void addLegalPersonSignatory(Long legalPerson)
    {
        this.legalPersonSignatories.add(legalPersonSignatory(legalPerson));
    }

    private ClosingLegalPerson legalPersonSignatory(Long legalPerson)
    {
        final var closingLegalPerson = new ClosingLegalPerson();
        closingLegalPerson.legalPerson = legalPerson;
        return closingLegalPerson;
    }
}
