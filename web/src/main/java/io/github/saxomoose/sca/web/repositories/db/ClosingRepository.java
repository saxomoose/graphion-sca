package io.github.saxomoose.sca.web.repositories.db;

import java.time.Instant;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import io.github.saxomoose.sca.web.models.Closing;

public interface ClosingRepository extends CrudRepository<Closing, Long> {
    @Query("SELECT *  FROM sca.closing LIMIT 1")
    Closing select();

    @Modifying
    @Query("""
            UPDATE sca.closing_signatory
            SET signature_timestamp = :signature_timestamp
            WHERE closing = :closing AND signatory = :signatory
            """)
    boolean update(Long closing, Long signatory, @Param("signature_timestamp") Instant timestamp);
}
