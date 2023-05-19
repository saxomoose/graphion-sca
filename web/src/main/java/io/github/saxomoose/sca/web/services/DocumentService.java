package io.github.saxomoose.sca.web.services;

import java.io.InputStream;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.saxomoose.sca.web.enums.Path;
import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.services.repositories.DocumentRepository;

@Service
public class DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final DocumentRepository documentRepository;
    private final MinIOService minioService;

    @Autowired
    public DocumentService(DocumentRepository documentRepository, MinIOService minioService)
    {
        this.documentRepository = documentRepository;
        this.minioService = minioService;
    }

    public Page<Document> selectPage(final Pageable pageable, final Long tenantId)
    {
        return documentRepository.findAllByTenant(pageable, tenantId);
    }

    public Document select(final Long tenantId, final UUID fileUUID)
    {
        return documentRepository.select(tenantId, fileUUID);
    }

    @Transactional
    public void create(final Tenant tenant, final String filename, final Long clientSideFileSize, final InputStream inputStream)
    {
        final var documentToCreate = Document.of(tenant.id(), filename);
        final Long documentId = documentRepository.save(documentToCreate).id();
        final UUID fileUUID = documentRepository.select(tenant.id(), documentId).fileUUID();
        final String filePath = filePath(Path.UPLOADS, fileUUID);
        minioService.configureBucket(tenant.bucket());
        minioService.storeObject(tenant.bucket(), filePath, inputStream);
        if (minioService.statObject(tenant.bucket(), filePath).size() != clientSideFileSize) {
            logger.error("The file size stored in the bucket does not match the client-side file size.");
            minioService.deleteObject(tenant.bucket(), filePath);
            throw new RuntimeException();
        }
    }

    public InputStream fileAsStream(String bucket, UUID fileUUID)
    {
        return minioService.objectAsStream(bucket, filePath(Path.UPLOADS, fileUUID));
    }

    private String filePath(Path path, final UUID fileUUID)
    {
        return String.format("%s%s", path, fileUUID);
    }
}
