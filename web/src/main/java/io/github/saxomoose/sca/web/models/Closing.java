package io.github.saxomoose.sca.web.models;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Closing {
    private final Long tenant;
    
    private final Long id;
    private final String name;
    private final LocalDateTime timestamp;
    private final ZoneId zoneId;
    private final String location;
    private final Boolean invitesSent;
    private final LocalDateTime expirationTimestamp;

    private Long createdBy;
    private Long lastUpdatedBy;
    private LocalDateTime createdTimestamp;
    private LocalDateTime lastUpdatedTimestamp;

    private Set<User> users;

    public static Closing withId(Long id, String name, LocalDateTime timestamp, ZoneId zoneId, String location)
    {
        return new Closing(null, id, name, timestamp, zoneId, location, null, null);
    }
    
    public static Closing of(Long tenantId, String name, LocalDateTime timestamp, ZoneId zoneId, String location)
    {
        return new Closing(tenantId, null, name, timestamp, zoneId, location, null, null);
    }

    public Closing(
        Long tenant, 
        Long id, 
        String name, 
        LocalDateTime timestamp, 
        ZoneId zoneId, 
        String location, 
        Boolean invitesSent,
        LocalDateTime expirationTimestamp)
    {
        this.tenant = tenant;
        this.id = id;
        this.name = name;
        this.zoneId = zoneId;
        this.timestamp = timestamp;
        this.location = location;
        this.invitesSent = invitesSent;
        this.expirationTimestamp = expirationTimestamp;
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

    public Boolean invitesSent()
    {
        return invitesSent;
    }

    public LocalDateTime expirationTimestamp()
    {
        return expirationTimestamp;
    }

    public ZonedDateTime zonedExpirationTimestamp()
    {
        return ZonedDateTime.of(expirationTimestamp(), zoneId());
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

    public Set<User> users()
    {
        return users;
    }

    public Closing addUser (User user)
    {
        if (users == null) {
            users = new HashSet<User>();
        }
        users.add(user);
        return this;
    }

    public Boolean isExpired()
    {
        if (zonedExpirationTimestamp().compareTo(ZonedDateTime.now(zoneId())) <= 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
}
