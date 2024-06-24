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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.saxomoose.sca.web.config.db.RowMapperCollection;
import io.github.saxomoose.sca.web.enums.PathEnum;
import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.models.Tenant;

@Service
public class DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final ObjectStorageService objectStorageService;
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final RowMapper<Document> rowMapper;

    @Autowired
    public DocumentService(
        ObjectStorageService objectStorageService, 
        NamedParameterJdbcOperations namedParameterJdbcOperations, 
        RowMapperCollection rowMappers)
    {
        this.objectStorageService = objectStorageService;
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.rowMapper = (RowMapper<Document>) rowMappers.getRowMapper(Document.class);
    }

    // Data
    // Aggregate
    public Long countDocumentsByTenant(Long tenantId) {
        String sql = """
        SELECT COUNT(*)
        FROM sca.document
        WHERE tenant = :tenantId; 
        """;
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("tenantId", tenantId);
        return namedParameterJdbcOperations.queryForObject(sql, params, Long.class);
    }

    // Read
    public List<Document> documentsByIds(List<Long> documentIds)
    {
        String sql = """
            SELECT *
            FROM sca.document
            WHERE id IN (:documentIds);    
        """;
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of("documentIds", documentIds));
        return namedParameterJdbcOperations.query(sql, params, rowMapper);
    }

    public Page<Document> documentsByTenant(Long tenantId, Pageable pageable)
    {
        Integer offset = pageable.getPageNumber() * pageable.getPageSize();
        String sql = """
            SELECT *
            FROM sca.document
            WHERE tenant = :tenantId
            ORDER BY filename
            LIMIT :pageSize OFFSET :offset;
        """;
        MapSqlParameterSource params = new MapSqlParameterSource(
            Map.of(
                "tenantId", tenantId,
                "pageSize", pageable.getPageSize(),
                "offset", offset));
        List<Document> tenantDocuments = namedParameterJdbcOperations.query(sql, params, rowMapper);
        return PageableExecutionUtils.getPage(tenantDocuments, pageable, () -> countDocumentsByTenant(tenantId));
    }

    public Document firstDocument()
    {
        String sql = """
        SELECT * FROM sca.document LIMIT 1;    
        """;
        return namedParameterJdbcOperations.getJdbcOperations().queryForObject(sql, rowMapper);
    }

    public Document documentById(Long documentId)
    {
        String sql = """
        SELECT * FROM sca.document WHERE id = :documentId;    
        """;
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("documentId", documentId);
        return namedParameterJdbcOperations.queryForObject(sql, params, rowMapper);
    }

    public Document documentByTenantAndFileUUID(Long tenantId, UUID fileUUID)
    {
        String sql = """
        SELECT * FROM sca.document WHERE tenant = :tenantId AND file_uuid = :fileUUID;    
        """;
        MapSqlParameterSource params = new MapSqlParameterSource(
            Map.of(
                "tenantId", tenantId,
                "fileUUID", fileUUID
            ));
        return namedParameterJdbcOperations.queryForObject(sql, params, rowMapper);
    }

    public InputStream fileAsStream(String bucket, UUID fileUUID)
    {
        return objectStorageService.objectAsStream(bucket, filePath(PathEnum.CLOSING_DOCUMENTS, fileUUID));
    }

    private String filePath(PathEnum path, UUID fileUUID)
    {
        return String.format("%s/%s", path, fileUUID);
    }

    // Update
    @Transactional
    public void create(
        Tenant tenant,
        Long principalId,
        String filename,
        Long clientSideFileSize, 
        InputStream inputStream)
    {
        Document documentToCreate = Document.of(tenant.id(), filename);
        UUID fileUUID = insert(documentToCreate, principalId).fileUUID();
        String filePath = filePath(PathEnum.CLOSING_DOCUMENTS, fileUUID);
        objectStorageService.storeObject(tenant.bucket(), filePath, inputStream);
        if (objectStorageService.statObject(tenant.bucket(), filePath).size() != clientSideFileSize) {
            logger.error("The file size stored in the bucket does not match the client-side file size.");
            objectStorageService.deleteObject(tenant.bucket(), filePath);
            throw new RuntimeException();
        }
    }

    public Document insert(Document document, Long principalId)
    {
        String sql = """
            INSERT INTO sca.document
            VALUES (:tenantId, DEFAULT, :filename, DEFAULT, :createdBy, :lastUpdatedBy, DEFAULT, DEFAULT)
            RETURNING *;        
        """;
        MapSqlParameterSource params = new MapSqlParameterSource(
            Map.of(
                "tenantId", document.tenantId(),
                "filename", document.filename(),
                "createdBy", principalId,
                "lastUpdatedBy", principalId));
        return namedParameterJdbcOperations.queryForObject(sql, params, rowMapper);
    }
}
