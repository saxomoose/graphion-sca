package io.github.saxomoose.sca.web.models.projections;

import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Page;
import io.github.saxomoose.sca.web.models.Closing;
import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.models.User;

public class ClosingsToIndex {
    private final Page<Closing> closingPage;
    private final Map<Closing, Set<User>> users;
    private final Map<Long, Map<User, Set<Document>>> documents;

    public ClosingsToIndex(
        Page<Closing> closingPage,
        Map<Closing, Set<User>> users,
        Map<Long, Map<User, Set<Document>>> documents)
    {
        this.closingPage = closingPage;
        this.users = users;
        this.documents = documents;
    }

    public Page<Closing> closingPage() 
    {
        return closingPage;
    }

    public Map<Closing, Set<User>> users() 
    {
        return users;
    }

    public Map<Long, Map<User, Set<Document>>> documents() 
    {
        return documents;
    }

    


}
