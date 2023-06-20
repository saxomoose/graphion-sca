package io.github.saxomoose.sca.web.models.transfer;

import java.util.HashSet;
import java.util.Set;
import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.models.User;

public class UserToIndex extends User {

    private Set<Document> documents;

    public UserToIndex(
        Long id,
        String username,
        String firstName,
        String lastName) {
        super(id, username, firstName, lastName, null, null, null);
    }

    public Set<Document> documents()
    {
        return documents;
    }

    public void addDocument (Document document)
    {
        if (documents == null) {
            documents = new HashSet<Document>();
        }
        documents.add(document);
    }
}
