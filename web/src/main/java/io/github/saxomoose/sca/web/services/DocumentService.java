package io.github.saxomoose.sca.web.services;

import io.github.saxomoose.sca.web.enums.DatabaseConstraint;
import io.github.saxomoose.sca.web.exceptions.DatabaseConstraintViolationException;
import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.repositories.DocumentRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final HttpSession httpSession;
    private final DocumentRepository documentRepository;
    private final ObjectStorageService objectStorageService;

    @Autowired
    public DocumentService(HttpSession httpSession, DocumentRepository documentRepository, ObjectStorageService objectStorageService) {
        this.httpSession = httpSession;
        this.documentRepository = documentRepository;
        this.objectStorageService = objectStorageService;
    }

    public Set<Document> getDocuments(final Long tenantId) {
        final var documents = documentRepository.findAllByTenant(tenantId);
        return new HashSet<>(documents);
    }

    public void create(
            final io.github.saxomoose.sca.web.dtos.Document documentDTO,
            final MultipartFile file
    ) throws IllegalArgumentException {
        final var createdFile = objectStorageService.storeFile(documentDTO.getFriendlyFilename(), file);
        final var document = Document.of(documentDTO.getFriendlyFilename(), createdFile.object(), ((Tenant) httpSession.getAttribute("tenant")).getId());
        try {
            documentRepository.save(document);
        } catch (DbActionExecutionException e) {
            if (e.getCause().getMessage().contains(DatabaseConstraint.unique_constraint_document_friendly_filename.name())) {
                logger.info(String.format("Document not created. friendly_name %s already exists for tenant %d.", document.getFriendlyFilename(), document.getTenant()));
                throw new IllegalArgumentException(new DatabaseConstraintViolationException(DatabaseConstraint.unique_constraint_document_friendly_filename));
            }
        }
    }

    public InputStreamResource getFile(Integer tenant, UUID documentName) {
//        return objectStorageService.getFileAsResource(documentName);
        // TODO
        byte[] bytes = {};
        return new InputStreamResource(new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        });
    }
}
