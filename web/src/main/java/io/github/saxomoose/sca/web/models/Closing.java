package io.github.saxomoose.sca.web.models;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
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
    private final LocalDateTime timestamp;
    private final ZoneId zoneId;
    private final String location;

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

    public static Closing overloadOf(Long id, String name, LocalDateTime timestamp, ZoneId zoneId, String location)
    {
        return new Closing(null, id, name, timestamp, zoneId, location);
    }

    public static Closing of(Long tenantId, String name, LocalDateTime timestamp, ZoneId zoneId, String location)
    {
        return new Closing(tenantId, null, name, timestamp, zoneId, location);
    }

    public Closing(Long tenant, Long id, String name, LocalDateTime timestamp, ZoneId zoneId, String location)
    {
        this.tenant = tenant;
        this.id = id;
        this.name = name;
        this.zoneId = zoneId;
        this.timestamp = timestamp;
        this.location = location;
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

    public LocalDateTime timestamp()
    {
        return timestamp;
    }

    public ZoneId zoneId()
    {
        return zoneId;
    }

    public ZonedDateTime zonedTimestamp()
    {
        return ZonedDateTime.of(timestamp(), zoneId());
    }

    public String location()
    {
        return location;
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


}
