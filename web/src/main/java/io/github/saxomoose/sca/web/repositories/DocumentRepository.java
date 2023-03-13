package io.github.saxomoose.sca.web.repositories;

import io.github.saxomoose.sca.web.models.Document;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface DocumentRepository extends CrudRepository<Document, Long> {
    @Query("SELECT *  FROM sca.document LIMIT 1")
    Document findFirst();

    Collection<Document> findAllByTenant(Long tenantId);

    @Query("SELECT *  FROM sca.document WHERE tenant = :tenant AND path = :path")
    Document find(@Param("tenant") Integer tenant, @Param("path") String path);
}
