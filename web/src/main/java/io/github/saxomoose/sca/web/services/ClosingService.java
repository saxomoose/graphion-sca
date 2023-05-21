package io.github.saxomoose.sca.web.services;

import java.time.Instant;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import io.github.saxomoose.sca.web.models.Closing;
import io.github.saxomoose.sca.web.services.repositories.ClosingRepository;

@Service
public class ClosingService {

    private NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final ClosingRepository closingRepository;

    @Autowired
    public ClosingService(NamedParameterJdbcOperations namedParameterJdbcOperations, ClosingRepository closingRepository)
    {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.closingRepository = closingRepository;
    }
    
    // Read
    public Page<Closing> closingPage(final Pageable pageable, final Long tenantId)
    {
        return closingRepository.findAllByTenant(pageable, tenantId);
    }

    // Update
    public void upsertNaturalPersonSignatory(Long closingId, Long signatoryId, Long documentId)
    {
        upsertNaturalPersonSignatory(closingId, signatoryId, documentId, null);
    }

    /**
     * signatureTimestamp has to be passed separately to allow null.
     */
    public void upsertNaturalPersonSignatory(Long closingId, Long signatoryId, Long documentId, Instant signatureTimestamp)
    {
        final var sql = """
            INSERT INTO sca.closing_signatory (closing, signatory, document, signature_timestamp)
            VALUES (:closingId, :signatoryId, :documentId, :signatureTimestamp)
            ON CONFLICT (closing, signatory, document) DO UPDATE
            SET document = :documentId, signature_timestamp = :signatureTimestamp;
        """;
        if (signatureTimestamp == null) {

        }

        final var params = new MapSqlParameterSource().addValues(
            Map.of(
                "closingId", closingId,
                "signatoryId", signatoryId,
                "documentId", documentId));
        params.addValue("signatureTimestamp", signatureTimestamp, java.sql.Types.TIMESTAMP_WITH_TIMEZONE);

        namedParameterJdbcOperations.update(sql, params);
    }

    public void upsertLegalPersonSignatory(Long closingId, Long legalPersonId)
    {
        upsertLegalPersonSignatory(closingId, legalPersonId, Boolean.FALSE);
    }

    public void upsertLegalPersonSignatory(Long closingId, Long legalPersonId, Boolean status)
    {
        final var sql = """
            INSERT INTO sca.closing_legal_person (closing, legal_person, status)
            VALUES (:closingId, :legalPersonId, :status)
            ON CONFLICT (closing, legal_person) DO UPDATE
            SET status = :status;
        """;
        final var params = new MapSqlParameterSource().addValues(
            Map.of(
                "closingId", closingId,
                "legalPersonId", legalPersonId,
                "status", status));

        namedParameterJdbcOperations.update(sql, params);
    }
}
