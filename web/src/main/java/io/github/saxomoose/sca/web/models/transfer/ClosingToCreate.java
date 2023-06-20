package io.github.saxomoose.sca.web.models.transfer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.models.User;

public class ClosingToCreate implements Serializable {
    private final String name;
    private final LocalDateTime timestamp;
    private final ZoneId zoneId;
    private final String location;
    private final LocalDateTime expirationTimestamp;
    private final List<Long> userIds;
    private final List<Long> documentIds;
    
    private List<User> users;
    private List<Document> documents;

    public ClosingToCreate(
        String name, 
        LocalDateTime timestamp, 
        ZoneId zoneId, 
        String location,
        LocalDateTime expirationTimestamp, 
        List<Long> userIds,
        List<Long> documentIds
    ) {
        this.name = name;
        this.timestamp = timestamp;
        this.zoneId = zoneId;
        this.location = location;
        this.expirationTimestamp = expirationTimestamp;
        this.userIds = userIds;
        this.documentIds = documentIds;
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

    public LocalDateTime expirationTimestamp()
    {
        return expirationTimestamp;
    }

    public List<Long> userIds() 
    {
        return userIds;
    }

    public List<Long> documentIds() 
    {
        return documentIds;
    }

    public List<User> users()
    {
        return users;
    }

    public List<Document> documents()
    {
        return documents;
    }

    public void setUsers(List<User> users)
    {
        this.users = users;
    }

    public void setDocuments(List<Document> documents)
    {
        this.documents = documents;
    }
}
