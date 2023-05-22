package io.github.saxomoose.sca.web.models;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

public class Closing {
    private final Long tenant;
    
    private final Long id;
    private final String name;
    private final LocalDateTime timestamp;
    private final ZoneId zoneId;
    private final String location;

    private Long createdBy;
    private Long lastUpdatedBy;
    private LocalDateTime createdTimestamp;
    private LocalDateTime lastUpdatedTimestamp;

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
