package io.github.saxomoose.sca.web.repositories.jdbc;

import io.github.saxomoose.sca.web.models.Closing;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface ClosingRepository extends CrudRepository<Closing, Long> {
    @Query("SELECT *  FROM sca.closing LIMIT 1")
    Closing select();

    @Modifying
    @Query("""
        UPDATE sca.closing_signatory
        SET signature_timestamp = :signature_timestamp
        WHERE closing = :closing AND signatory = :signatory
    """)
    boolean update(@Param("closing") Long closing, @Param("signatory") Long signatory, @Param("signature_timestamp") Instant timestamp);
}
