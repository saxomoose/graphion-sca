package io.github.saxomoose.sca.web.services;

import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.repositories.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Boolean exists(final Long tenant, final UUID fileUUID) {
        final var path = generatePath(fileUUID);
        return documentRepository.exists(tenant, path);
    }

    public Set<Document> getDocuments(final Long tenantId) {
        final var documents = documentRepository.findAllByTenant(tenantId);
        return new HashSet<>(documents);
    }

    public Document getDocument(final Long tenant, final UUID fileUUID) {
        final var path = generatePath(fileUUID);
        return documentRepository.find(tenant, path);
    }

    public void insert(final Long tenantId, final String filename, final String path){
        final var document = Document.of(filename, path, tenantId);
        documentRepository.save(document);
    }

    public String generatePath(final UUID fileUUID) {
        return String.format("/files/%s", fileUUID);
    }
}
