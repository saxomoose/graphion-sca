package io.github.saxomoose.sca.web.services.repositories;

import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import io.github.saxomoose.sca.web.models.Closing;

public interface ClosingRepository extends CrudRepository<Closing, Long>, PagingAndSortingRepository<Closing, Long> {
    // Read
    Page<Closing> findAllByTenant(Pageable pageable, Long tenantId);

    @Query("SELECT *  FROM sca.closing LIMIT 1")
    Closing firstClosing();

    // Update
    @Modifying
    @Query("""
            UPDATE sca.closing_signatory
            SET signature_timestamp = :signature_timestamp
            WHERE closing = :closing AND signatory = :signatory
            """)
    boolean update(Long closing, Long signatory, @Param("signature_timestamp") Instant timestamp);
}
