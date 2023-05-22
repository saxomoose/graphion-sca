package io.github.saxomoose.sca.web.services;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.saxomoose.sca.web.config.db.RowMapperCollection;
import io.github.saxomoose.sca.web.enums.Path;
import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.models.Tenant;

@Service
public class DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final MinIOService minioService;
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final RowMapperCollection rowMappers;

    @Autowired
    public DocumentService(MinIOService minioService, NamedParameterJdbcOperations namedParameterJdbcOperations, RowMapperCollection rowMappers)
    {
        this.minioService = minioService;
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.rowMappers = rowMappers;
    }

    // Aggregate
    public Long countDocumentsByTenant(Long tenantId) {
        final var sql = """
        SELECT COUNT(*)
        FROM sca.document
        WHERE tenant = :tenantId; 
        """;
        final var params = new MapSqlParameterSource().addValue("tenantId", tenantId);
        return namedParameterJdbcOperations.queryForObject(sql, params, Long.class);
    }

    // Read
    public Page<Document> documentsByTenant(final Long tenantId, final Pageable pageable)
    {
        final Integer offset = pageable.getPageNumber() * pageable.getPageSize();
        final var sql = """
            SELECT *
            FROM sca.document
            WHERE tenant = :tenantId
            LIMIT :pageSize OFFSET :offset;
        """;
        final var params = new MapSqlParameterSource(
            Map.of(
                "tenantId", tenantId,
                "pageSize", pageable.getPageSize(),
                "offset", offset));
        final List<Document> tenantDocuments = namedParameterJdbcOperations.query(sql, params, rowMappers.getRowMapper(Document.class));
        return PageableExecutionUtils.getPage(tenantDocuments, pageable, () -> countDocumentsByTenant(tenantId));
    }

    public Document firstDocument()
    {
        final var sql = """
        SELECT * FROM sca.document LIMIT 1;    
        """;
        return namedParameterJdbcOperations.getJdbcOperations().queryForObject(sql, rowMappers.getRowMapper(Document.class));
    }

    public Document documentById(final Long documentId)
    {
        final var sql = """
        SELECT * FROM sca.document WHERE id = :documentId;    
        """;
        final var params = new MapSqlParameterSource().addValue("documentId", documentId);
        return namedParameterJdbcOperations.queryForObject(sql, params, rowMappers.getRowMapper(Document.class));
    }

    public Document documentByTenantAndFileUUID(final Long tenantId, final UUID fileUUID)
    {
        final var sql = """
        SELECT * FROM sca.document WHERE tenant = :tenantId AND file_uuid = :fileUUID;    
        """;
        final var params = new MapSqlParameterSource(
            Map.of(
                "tenantId", tenantId,
                "fileUUID", fileUUID
            ));
        return namedParameterJdbcOperations.queryForObject(sql, params, rowMappers.getRowMapper(Document.class));
    }

    public InputStream fileAsStream(String bucket, UUID fileUUID)
    {
        return minioService.objectAsStream(bucket, filePath(Path.UPLOADS, fileUUID));
    }

    private String filePath(Path path, final UUID fileUUID)
    {
        return String.format("%s%s", path, fileUUID);
    }

    // Update
    @Transactional
    public void create(final Tenant tenant, final Long principalId, final String filename, final Long clientSideFileSize, final InputStream inputStream)
    {
        final var documentToCreate = Document.of(tenant.id(), filename);
        final UUID fileUUID = insert(documentToCreate, principalId).fileUUID();
        final String filePath = filePath(Path.UPLOADS, fileUUID);
        minioService.configureBucket(tenant.bucket());
        minioService.storeObject(tenant.bucket(), filePath, inputStream);
        if (minioService.statObject(tenant.bucket(), filePath).size() != clientSideFileSize) {
            logger.error("The file size stored in the bucket does not match the client-side file size.");
            minioService.deleteObject(tenant.bucket(), filePath);
            throw new RuntimeException();
        }
    }

    public Document insert(Document document, Long principalId)
    {
        final var sql = """
            INSERT INTO sca.document
            VALUES (:tenantId, DEFAULT, :filename, DEFAULT, :createdBy, :lastUpdatedBy, DEFAULT, DEFAULT)
            RETURNING *;        
        """;
        final var params = new MapSqlParameterSource(
            Map.of(
                "tenantId", document.tenantId(),
                "filename", document.filename(),
                "createdBy", principalId,
                "lastUpdatedBy", principalId));
        return namedParameterJdbcOperations.queryForObject(sql, params, rowMappers.getRowMapper(Document.class));
    }
}
