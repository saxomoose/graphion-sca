package io.github.saxomoose.sca.web.repositories.db;

import java.util.Collection;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import io.github.saxomoose.sca.web.models.Document;

public interface DocumentRepository extends CrudRepository<Document, Long> {
    @Query("SELECT *  FROM sca.document LIMIT 1")
    Document select();

    Collection<Document> findAllByTenant(Long tenantId);

    @Query("SELECT *  FROM sca.document WHERE tenant = :tenant AND path = :path")
    Document select(@Param("tenant") Long tenant, @Param("path") String path);

    @Query("SELECT EXISTS (SELECT 1 FROM sca.document WHERE tenant = :tenant AND path = :path)")
    Boolean exists(@Param("tenant") Long tenant, @Param("path") String path);
}
