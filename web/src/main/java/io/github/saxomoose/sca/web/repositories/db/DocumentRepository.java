package io.github.saxomoose.sca.web.repositories.db;

import java.util.Collection;
import java.util.UUID;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import io.github.saxomoose.sca.web.models.Document;

public interface DocumentRepository extends CrudRepository<Document, Long> {
    @Query("SELECT *  FROM sca.document LIMIT 1")
    Document select();

    Collection<Document> findAllByTenant(Long tenantId);

    @Query("SELECT *  FROM sca.document WHERE tenant = :tenant AND file_uuid = :fileUUID")
    Document select(Long tenant, UUID fileUUID);

    @Query("SELECT EXISTS (SELECT 1 FROM sca.document WHERE tenant = :tenant AND file_uuid = :fileUUID)")
    Boolean exists(Long tenant, UUID fileUUID);
}
