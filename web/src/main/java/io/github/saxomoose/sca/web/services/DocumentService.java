package io.github.saxomoose.sca.web.services;

import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;
import io.github.saxomoose.sca.web.exceptions.ObjectStorageException;
import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.repositories.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final DocumentRepository documentRepository;
    private final ObjectStorageService objectStorageService;

    @Autowired
    public DocumentService(DocumentRepository documentRepository, ObjectStorageService objectStorageService) {
        this.documentRepository = documentRepository;
        this.objectStorageService = objectStorageService;
    }

    public Set<Document> getDocuments(final Long tenantId) {
        final var documents = documentRepository.findAllByTenant(tenantId);
        return new HashSet<>(documents);
    }

    public void create(
            final Tenant tenant,
            final io.github.saxomoose.sca.web.dtos.Document documentDTO,
            Long clientSideFileSize, final InputStream inputStream
            ) throws ObjectStorageException, BusinessLogicException {
        final var name = UUID.nameUUIDFromBytes(documentDTO.getFilename().getBytes());
        final var path = String.format("/documents/%s", name);
        // check uniqueness
        if(documentExists(tenant.getId(), name)) {
            logger.info(String.format("Document not created. filename %s already exists for tenant %d.", documentDTO.getFilename(), tenant.getId()));
            throw new BusinessLogicException("document.filename.duplicate");
        }
        objectStorageService.storeFile(tenant.getBucket(), path, clientSideFileSize, inputStream);
        final var document = Document.of(documentDTO.getFilename(), path, tenant.getId());
        documentRepository.save(document);
    }

    public Boolean documentExists(Long tenant, UUID documentName) {
        final var path = String.format("/documents/%s", documentName);
        return documentRepository.exists(tenant, path);
    }

    public Document getDocument(Long tenant, UUID documentName) {
        final var path = String.format("/documents/%s", documentName);
        return documentRepository.find(tenant, path);
    }

    public InputStreamResource getFile(String bucket, String path) throws ObjectStorageException {
        return new InputStreamResource(objectStorageService.getObjectAsStream(bucket, path));
    }
}
