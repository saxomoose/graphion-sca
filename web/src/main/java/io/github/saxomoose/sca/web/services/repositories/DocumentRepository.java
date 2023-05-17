package io.github.saxomoose.sca.web.services.repositories;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import io.github.saxomoose.sca.web.models.Document;

public interface DocumentRepository extends CrudRepository<Document, Long>, PagingAndSortingRepository<Document, Long> {
    @Query("SELECT *  FROM sca.document LIMIT 1")
    Document selectFirst();

    Page<Document> findAllByTenant(Pageable pageable, Long tenantId);

    @Query("SELECT * FROM sca.document WHERE id = :id")
    Document select(Long tenant, Long id);

    @Query("SELECT *  FROM sca.document WHERE tenant = :tenant AND file_uuid = :fileUUID")
    Document select(Long tenant, UUID fileUUID);

    @Query("SELECT EXISTS (SELECT 1 FROM sca.document WHERE tenant = :tenant AND file_uuid = :fileUUID)")
    Boolean exists(Long tenant, UUID fileUUID);
}
