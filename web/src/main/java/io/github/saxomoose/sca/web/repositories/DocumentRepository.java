package io.github.saxomoose.sca.web.repositories;

import io.github.saxomoose.sca.web.models.Document;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface DocumentRepository extends CrudRepository<Document, Long> {
    @Query("SELECT *  FROM sca.document LIMIT 1")
    Document findFirst();
}
