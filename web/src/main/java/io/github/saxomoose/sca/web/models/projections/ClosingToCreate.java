package io.github.saxomoose.sca.web.models.projections;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class ClosingToCreate {
    private final String name;
    private final LocalDateTime timestamp;
    private final ZoneId zoneId;
    private final String location;
    private final LocalDateTime expirationTimestamp;

    private final List<Long> users;
    private final List<Long> documents;

    public ClosingToCreate(
        String name, 
        LocalDateTime timestamp, 
        ZoneId zoneId, 
        String location,
        LocalDateTime expirationTimestamp, 
        List<Long> users,
        List<Long> documents
    ) {
        this.name = name;
        this.timestamp = timestamp;
        this.zoneId = zoneId;
        this.location = location;
        this.expirationTimestamp = expirationTimestamp;
        this.users = users;
        this.documents = documents;
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

    public String location() 
    {
        return location;
    }

    public List<Long> users() 
    {
        return users;
    }

    public List<Long> documents() 
    {
        return documents;
    }

    

    
    
}
