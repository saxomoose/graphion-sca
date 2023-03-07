package io.github.saxomoose.sca.web.services;

import io.github.saxomoose.sca.web.exceptions.UniqueConstraintViolationException;
import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.repositories.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

@Service
public class DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final DocumentRepository documentRepository;
    private final FileStorageService fileStorageService;

    public DocumentService(DocumentRepository documentRepository, FileStorageService fileStorageService) {
        this.documentRepository = documentRepository;
        this.fileStorageService = fileStorageService;
    }

    public Set<Document> getDocuments(Long tenantId) {
        final var documents = documentRepository.findAllByTenant(tenantId);

        return new HashSet<>(documents);
    }

    public void create(io.github.saxomoose.sca.web.dtos.Document documentDTO, MultipartFile file, Long tenantId) throws UniqueConstraintViolationException {
        final var document = Document.of(documentDTO.getFriendlyFilename(), "test1_path", tenantId);
        try {
            documentRepository.save(document);
        } catch (DbActionExecutionException e) {
            if (e.getCause().getMessage().contains("unique_constraint_document")) {
                logger.info(String.format("Document not created. path %s already exists for tenant %d.", document.getPath(), document.getTenant()));
                throw new UniqueConstraintViolationException();
            }
        }
    }
}
