package io.github.saxomoose.sca.web.models.projections;

import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Page;
import io.github.saxomoose.sca.web.models.Document;

public class ClosingPageToIndex {
    private final Page<ClosingToIndex> closingPage;
    private final Map<ClosingToIndex, Set<UserToIndex>> users;
    private final Map<Long, Map<UserToIndex, Set<Document>>> documents;

    public ClosingPageToIndex(
        Page<ClosingToIndex> closingPage,
        Map<ClosingToIndex, Set<UserToIndex>> users,
        Map<Long, Map<UserToIndex, Set<Document>>> documents)
    {
        this.closingPage = closingPage;
        this.users = users;
        this.documents = documents;
    }

    public Page<ClosingToIndex> closingPage() 
    {
        return closingPage;
    }

    public Map<ClosingToIndex, Set<UserToIndex>> users() 
    {
        return users;
    }

    public Map<Long, Map<UserToIndex, Set<Document>>> documents() 
    {
        return documents;
    }

    


}
