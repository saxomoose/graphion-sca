package io.github.saxomoose.sca.web.services;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.models.User;

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
            SELECT
                c.id AS closing_id, c.name AS closing_name, c.timestamp, c.zone_id, c.location, 
                u.id AS user_id, u.first_name, u.last_name, u.username,
                d.id AS document_id, d.filename, cs.signature_timestamp 
            FROM sca.closing c
            JOIN sca.closing_signatory cs ON c.id = cs.closing
            JOIN sca.user u ON cs.signatory = u.id
            JOIN sca.document d ON cs.document = d.id
            WHERE c.tenant = :tenantId
            LIMIT :pageSize OFFSET :offset;
        """;
        final var params = new MapSqlParameterSource(
            Map.of(
                "tenantId", tenantId,
                "pageSize", pageable.getPageSize(),
                "offset", offset));
        final List<Closing> tenantClosings = namedParameterJdbcOperations.query(sql, params, (rs) -> {
            final var closingsMap = new LinkedHashMap<Long, Closing>();
            final var usersMap = new HashMap<Long, User>();
            while (rs.next()) {
                final var closing = Closing.withId(
                    rs.getLong("closing_id"),
                    rs.getString("closing_name"),
                    rs.getTimestamp("timestamp").toLocalDateTime(),
                    ZoneId.of(rs.getString("zone_id")),
                    rs.getString("location"));
                if (!closingsMap.containsKey(closing.id())) {
                    usersMap.clear();
                    closingsMap.put(closing.id(), closing);
                }
                final var user = User.withId(
                    rs.getLong("user_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("username"));
                usersMap.putIfAbsent(user.id(), user);
                final var document = Document.withId(
                    rs.getLong("document_id"),
                    rs.getString("filename"));
                final var signatureTimestamp = rs.getTimestamp("signature_timestamp");
                if (signatureTimestamp != null) {
                    document.setSignatureTimestamp(signatureTimestamp.toLocalDateTime());
                } else {
                    document.setSignatureTimestamp(null);
                }
                usersMap.get(user.id()).addDocument(document);
                closingsMap.get(closing.id()).addUser(user);    
            }
            
            return new ArrayList<Closing>(closingsMap.values());
        });
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
    
    public void insertNaturalPersonSignatory(Long closingId, Long signatoryId, Long documentId)
    {
        final var sql = """
            INSERT INTO sca.closing_signatory (closing, signatory, document, signature_timestamp)
            VALUES (:closingId, :signatoryId, :documentId, DEFAULT);
        """;
        final var params = new MapSqlParameterSource().addValues(
            Map.of(
                "closingId", closingId,
                "signatoryId", signatoryId,
                "documentId", documentId));

        namedParameterJdbcOperations.update(sql, params);
    }

    public void insertLegalPersonSignatory(Long closingId, Long legalPersonId)
    {
        final var sql = """
            INSERT INTO sca.closing_legal_person (closing, legal_person, status)
            VALUES (:closingId, :legalPersonId, DEFAULT);
        """;
        final var params = new MapSqlParameterSource().addValues(
            Map.of(
                "closingId", closingId,
                "legalPersonId", legalPersonId));

        namedParameterJdbcOperations.update(sql, params);
    }
}
