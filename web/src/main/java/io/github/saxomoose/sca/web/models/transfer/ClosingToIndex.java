package io.github.saxomoose.sca.web.models.transfer;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import io.github.saxomoose.sca.web.models.Closing;

public class ClosingToIndex extends Closing {

    private Set<UserToIndex> users;

    public ClosingToIndex(
        Long id,
        String name,
        LocalDateTime timestamp,
        ZoneId zoneId,
        String location,
        LocalDateTime expirationTimestamp, Boolean invitesSent) {
        super(null, id, name, timestamp, zoneId, location, expirationTimestamp, invitesSent);
    }

    public Set<UserToIndex> users()
    {
        return users;
    }

    public Closing addUser (UserToIndex user)
    {
        if (users == null) {
            users = new HashSet<UserToIndex>();
        }
        users.add(user);
        return this;
    }
}
