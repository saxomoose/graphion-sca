package io.github.saxomoose.sca.web.services;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import io.github.saxomoose.sca.web.config.db.RowMapperCollection;
import io.github.saxomoose.sca.web.models.Closing;

@Service
public class ClosingService {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final RowMapperCollection rowMappers;

    @Autowired
    public ClosingService(NamedParameterJdbcOperations namedParameterJdbcOperations, RowMapperCollection rowMappers)
    {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.rowMappers = rowMappers;
    }

    // Aggregate
    public Long countClosingsByTenant(Long tenantId) {
        final var sql = """
        SELECT COUNT(*)
        FROM sca.closing
        WHERE tenant = :tenantId; 
        """;
        final var params = new MapSqlParameterSource().addValue("tenantId", tenantId);
        return namedParameterJdbcOperations.queryForObject(sql, params, Long.class);
    }
    
    //Read
    public Closing firstClosing()
    {
        final var sql = """
        SELECT * FROM sca.closing LIMIT 1;    
        """;
        return namedParameterJdbcOperations.getJdbcOperations().queryForObject(sql, rowMappers.getRowMapper(Closing.class));
    }


    public Page<Closing> closingsByTenant(final Long tenantId, final Pageable pageable)
    {
        final Integer offset = pageable.getPageNumber() * pageable.getPageSize();
        final var sql = """
            SELECT *
            FROM sca.closing
            WHERE tenant = :tenantId
            LIMIT :pageSize OFFSET :offset;
        """;
        final var params = new MapSqlParameterSource(
            Map.of(
                "tenantId", tenantId,
                "pageSize", pageable.getPageSize(),
                "offset", offset));
        final List<Closing> tenantClosings = namedParameterJdbcOperations.query(sql, params, rowMappers.getRowMapper(Closing.class));
        return PageableExecutionUtils.getPage(tenantClosings, pageable, () -> countClosingsByTenant(tenantId));
    }
    
    // Update
    public Closing insert(Closing closing, Long principalId)
    {
        final var sql = """
        INSERT INTO sca.closing
        VALUES (:tenantId, DEFAULT, :name, :timestamp, :zoneId, :location, :createdBy, :lastUpdatedBy, DEFAULT, DEFAULT)
        RETURNING *;        
        """;
        final var params = new MapSqlParameterSource(
            Map.of(
                "tenantId", closing.tenantId(),
                "name", closing.name(),
                "timestamp", closing.timestamp(),
                "location", closing.location(),
                "createdBy", principalId,
                "lastUpdatedBy", principalId));
        params.addValue("zoneId", closing.zoneId().getId(), java.sql.Types.VARCHAR);
        return namedParameterJdbcOperations.queryForObject(sql, params, rowMappers.getRowMapper(Closing.class));
    }

    public void upsertNaturalPersonSignatory(Long closingId, Long signatoryId, Long documentId)
    {
        upsertNaturalPersonSignatory(closingId, signatoryId, documentId, null);
    }
    
    public void upsertNaturalPersonSignatory(Long closingId, Long signatoryId, Long documentId, Instant signatureTimestamp)
    {
        final var sql = """
            INSERT INTO sca.closing_signatory (closing, signatory, document, signature_timestamp)
            VALUES (:closingId, :signatoryId, :documentId, :signatureTimestamp)
            ON CONFLICT (closing, signatory, document) DO UPDATE
            SET document = :documentId, signature_timestamp = :signatureTimestamp;
        """;
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
