package io.github.saxomoose.sca.web.services;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import eu.europa.esig.dss.model.DSSDocument;
import io.github.saxomoose.sca.web.config.db.RowMapperCollection;
import io.github.saxomoose.sca.web.enums.PathEnum;
import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;
import io.github.saxomoose.sca.web.models.Closing;
import io.github.saxomoose.sca.web.models.ClosingSignatory;
import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.models.User;
import io.github.saxomoose.sca.web.models.projections.ClosingToIndex;
import io.github.saxomoose.sca.web.utils.Notification;

@Service
public class ClosingService {
    private final ObjectStorageService objectStorageService;
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final RowMapper<Closing> closingRowMapper;
    private final RowMapper<ClosingSignatory> closingSignatoryRowMapper;

    @Autowired
    public ClosingService(ObjectStorageService objectStorageService, NamedParameterJdbcOperations namedParameterJdbcOperations, RowMapperCollection rowMappers)
    {   
        this.objectStorageService = objectStorageService;
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.closingRowMapper = (RowMapper<Closing>) rowMappers.getRowMapper(Closing.class);
        this.closingSignatoryRowMapper = (RowMapper<ClosingSignatory>) rowMappers.getRowMapper(ClosingSignatory.class);

    }

    // Data
    // Aggregate
    public Long count() {
        final String sql = """
        SELECT COUNT(*)
        FROM sca.closing;
        """;
        return namedParameterJdbcOperations.getJdbcOperations().queryForObject(sql, Long.class);
    }

    public Long countClosingsByTenant(final Long tenantId) {
        final String sql = """
        SELECT COUNT(*)
        FROM sca.closing
        WHERE tenant = :tenantId; 
        """;
        final MapSqlParameterSource params = new MapSqlParameterSource().addValue("tenantId", tenantId);
        return namedParameterJdbcOperations.queryForObject(sql, params, Long.class);
    }
    
    //Read
    public Closing firstClosing()
    {
        final String sql = """
            SELECT * FROM sca.closing LIMIT 1;    
        """;
        return namedParameterJdbcOperations.getJdbcOperations().queryForObject(sql, closingRowMapper);
    }

    public Closing closingById(final Long closingId)
    {
        final String sql = """
            SELECT * FROM sca.closing WHERE id = :closingId;     
        """;
        final var params = new MapSqlParameterSource(Map.of("closingId", closingId));
        return namedParameterJdbcOperations.queryForObject(sql, params, closingRowMapper);
    }

    public ClosingToIndex closingsByTenant(final Long tenantId, final Pageable pageable)
    {
        final Integer offset = pageable.getPageNumber() * pageable.getPageSize();
        final String sql = """
            SELECT
                c.id AS closing_id, c.name AS closing_name, c.timestamp, c.zone_id, c.location, 
                u.id AS user_id, u.first_name, u.last_name, u.username,
                d.id AS document_id, d.filename, d.file_uuid, cs.signature_timestamp 
            FROM sca.closing c
            JOIN sca.closing_signatory cs ON c.id = cs.closing
            JOIN sca.user u ON cs.signatory = u.id
            JOIN sca.document d ON cs.document = d.id
            WHERE c.tenant = :tenantId
            ORDER BY :sort_order
            LIMIT :pageSize OFFSET :offset;
        """;
        final MapSqlParameterSource params = 
            new MapSqlParameterSource(
                Map.of(
                    "tenantId", tenantId,
                    "pageSize", pageable.getPageSize(),
                    "offset", offset,
                    "sort_order", pageable.getSort().stream().findFirst().get().getProperty()));
        final List<Closing> tenantClosings = namedParameterJdbcOperations.query(sql, params, (rs) -> {
            final Map<Long, Closing> closingsMap = new HashMap<Long, Closing>();
            final Map<Long, User> usersMap = new HashMap<Long, User>();
            while (rs.next()) {
                final Closing closing = Closing.withId(
                    rs.getLong("closing_id"),
                    rs.getString("closing_name"),
                    rs.getTimestamp("timestamp").toLocalDateTime(),
                    ZoneId.of(rs.getString("zone_id")),
                    rs.getString("location"));
                if (!closingsMap.containsKey(closing.id())) {
                    usersMap.clear();
                    closingsMap.put(closing.id(), closing);
                }
                final User user = User.withId(
                    rs.getLong("user_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("username"));
                usersMap.putIfAbsent(user.id(), user);
                final Document document = Document.withId(
                    rs.getLong("document_id"),
                    rs.getString("filename"),
                    UUID.fromString(rs.getString("file_uuid")));
                final Timestamp signatureTimestamp = rs.getTimestamp("signature_timestamp");
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
        
        final Page<Closing> closingPage = 
            PageableExecutionUtils.getPage(tenantClosings, pageable, () -> countClosingsByTenant(tenantId));
        final Map<Closing, Set<User>> users = 
            tenantClosings.stream().collect(Collectors.toMap(closing -> closing, Closing::users));
        final Map<Long, Map<User, Set<Document>>> documents = 
            tenantClosings.stream()
                .collect(
                    Collectors.toMap(
                        Closing::id, 
                        closing -> closing.users().stream().collect(Collectors.toMap(user -> user, user -> user.documents()))));

        return new ClosingToIndex(closingPage, users, documents);

    }

    public ClosingSignatory closingSignatory(final Long closingId, final Long userId, final UUID fileUUID)
    {
        final String sql = """
            SELECT *
            FROM sca.closing_signatory AS cs
            JOIN sca.document AS d ON d.id = cs.document
            WHERE cs.closing = :closingId AND cs.signatory = :userId AND d.file_uuid = :fileUUID;        
        """;
        final MapSqlParameterSource params = 
            new MapSqlParameterSource(
                Map.of(
                    "closingId", closingId,
                    "userId", userId,
                    "fileUUID", fileUUID));
        return namedParameterJdbcOperations.queryForObject(sql, params, closingSignatoryRowMapper);
    }

    public Boolean userIsLastSignatory(final Long closingId)
    {
        final var sql  = """
            SELECT COUNT(*)
            FROM sca.closing_signatory
            WHERE closing = :closingId AND signature_timestamp IS NULL;        
        """;
        final MapSqlParameterSource params =
            new MapSqlParameterSource(Map.of("closingId", closingId));
        final Long count = namedParameterJdbcOperations.queryForObject(sql, params, Long.class);
        return count == 1L ? Boolean.TRUE : Boolean.FALSE;
    }

    // Update
    public Closing insert(final Closing closing, final Long principalId)
    {
        final LocalDateTime expirationTimestamp = closing.expirationTimestamp();
        if (expirationTimestamp == null) {
            closing.setExpirationTimestamp(closing.timestamp().plusDays(30));
        }

        final String sql = """
        INSERT INTO sca.closing
        VALUES (:tenantId, DEFAULT, :name, :timestamp, :zoneId, :location, :expirationTimestamp, DEFAULT, :createdBy, :lastUpdatedBy, DEFAULT, DEFAULT)
        RETURNING *;        
        """;
        final MapSqlParameterSource params = 
            new MapSqlParameterSource(
                Map.of(
                    "tenantId", closing.tenantId(),
                    "name", closing.name(),
                    "timestamp", closing.timestamp(),
                    "location", closing.location(),
                    "expirationTimestamp", closing.expirationTimestamp(),
                    "createdBy", principalId,
                    "lastUpdatedBy", principalId));
        params.addValue("zoneId", closing.zoneId().getId(), java.sql.Types.VARCHAR);
        return namedParameterJdbcOperations.queryForObject(sql, params, closingRowMapper);
    }
    
    public void insertNaturalPersonSignatories(List<ClosingSignatory> closingSignatories) {
        final String sql = """
            INSERT INTO sca.closing_signatory (closing, signatory, document)
            VALUES (:closingId, :signatoryId, :documentId);
        """;
        final List<MapSqlParameterSource> batchParams = new ArrayList<>();
        for (final ClosingSignatory closingSignatory : closingSignatories) {
            batchParams.add(
                new MapSqlParameterSource(
                    Map.of(
                        "closingId", closingSignatory.closingId(),
                        "signatoryId", closingSignatory.signatoryId(),
                        "documentId", closingSignatory.documentId())));
        }
        namedParameterJdbcOperations.batchUpdate(sql, batchParams.toArray(new MapSqlParameterSource[0]));
    }

    public void insertLegalPersonSignatory(final Long closingId, final Long legalPersonId)
    {
        final String sql = """
            INSERT INTO sca.closing_legal_person (closing, legal_person, status)
            VALUES (:closingId, :legalPersonId, DEFAULT);
        """;
        final MapSqlParameterSource params = 
            new MapSqlParameterSource().addValues(
                Map.of(
                    "closingId", closingId,
                    "legalPersonId", legalPersonId));
        namedParameterJdbcOperations.update(sql, params);
    }

    @Transactional
    public void storeDocument(
        Closing closing,
        Long userId,
        String bucket,
        UUID fileUUID,
        DSSDocument signedDocument,
        Instant signatureTimestamp)
    {
        final LocalDateTime signatureTimestampAtClosingZoneId = ZonedDateTime.ofInstant(signatureTimestamp, closing.zoneId()).toLocalDateTime();

        objectStorageService.storeObject(
            bucket, 
            String.format("%s/%s", PathEnum.SIGNED, fileUUID),
            signedDocument.openStream());

        final String sql = """
            UPDATE sca.closing_signatory
            SET signature_timestamp = :signatureTimestamp
            WHERE closing = :closingId
                AND signatory = :userId
                AND document = (SELECT id FROM sca.document WHERE file_uuid = :fileUUID);
        """;
        final MapSqlParameterSource params = 
            new MapSqlParameterSource(
                Map.of(
                "signatureTimestamp", signatureTimestampAtClosingZoneId,
                "closingId", closing.id(),
                "userId", userId,
                "fileUUID", fileUUID));
        namedParameterJdbcOperations.update(sql, params);
    }

    // Service
    public void closingIsExpired(final Long closingId) throws BusinessLogicException
    {
        if (closingById(closingId).isExpired()) {
            throw new BusinessLogicException(new Notification(Notification.Type.ERROR, "TODO", "TODO"));
        }
    }

    public void userStatusOnClosing(Long closingId, Long userId, UUID fileUUID) throws BusinessLogicException
    {
        ClosingSignatory closingSignatory;
        try {
            closingSignatory = closingSignatory(closingId, userId, fileUUID);
        } catch (EmptyResultDataAccessException exception) {
            throw new BusinessLogicException(new Notification(Notification.Type.ERROR, "TODO", "TODO"));
        }
        if (closingSignatory.signatureTimestamp() != null) {
            throw new BusinessLogicException(new Notification(Notification.Type.ERROR, "TODO", "TODO"));
        }
    }

    @Transactional
    public void create(final Closing closingToCreate, final Long principalId, final List<ClosingSignatory> closingSignatories) {
        final Closing createdClosing = insert(closingToCreate, principalId);
        for (final ClosingSignatory closingSignatory : closingSignatories) {
            closingSignatory.setClosingId(createdClosing.id());
        }
        insertNaturalPersonSignatories(closingSignatories);
    }
}
