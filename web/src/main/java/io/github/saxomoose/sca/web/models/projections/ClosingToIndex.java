package io.github.saxomoose.sca.web.models.projections;

import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Page;
import io.github.saxomoose.sca.web.models.Closing;
import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.models.User;

public class ClosingToIndex {
    private final Page<Closing> closingPage;
    private final Map<Long, Set<User>> closingUsers;
    private final Map<Long, Map<Long, Set<Document>>> userDocuments;

    public ClosingToIndex(
        Page<Closing> closingPage,
        Map<Long, Set<User>> closingUsers,
        Map<Long, Map<Long, Set<Document>>> userDocuments)
    {
        this.closingPage = closingPage;
        this.closingUsers = closingUsers;
        this.userDocuments = userDocuments;
    }

    public Page<Closing> closingPage() 
    {
        return closingPage;
    }

    public Map<Long, Set<User>> closingUsers() 
    {
        return closingUsers;
    }

    public Map<Long, Map<Long, Set<Document>>> userDocuments() 
    {
        return userDocuments;
    }

    


}
