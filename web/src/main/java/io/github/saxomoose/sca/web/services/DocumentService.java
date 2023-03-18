package io.github.saxomoose.sca.web.services;

import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;
import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.repositories.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

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

    // CRUD
    public Set<Document> read(final Long tenantId) {
        final var documents = documentRepository.findAllByTenant(tenantId);
        return new HashSet<>(documents);
    }

    public Document read(final Long tenant, final UUID fileUUID) {
        final var path = generatePath(fileUUID);
        return documentRepository.select(tenant, path);
    }

    public void create(final Long tenantId, final String filename, final String path){
        final var document = Document.of(filename, path, tenantId);
        documentRepository.save(document);
    }

    public String generatePath(final UUID fileUUID) {
        return String.format("/files/%s", fileUUID);
    }

    private Boolean exists(final Long tenant, final UUID fileUUID) {
        final var path = generatePath(fileUUID);
        return documentRepository.exists(tenant, path);
    }

    // Validation
    public UUID convertToFileUUID(Long tenantId, String filename) throws BusinessLogicException {
        final var messageKey = "document.filename.invalid";
        if (filename.isBlank()) {
            throw new BusinessLogicException(messageKey);
        }
        final var pattern = Pattern.compile("^[a-zA-Z0-9_-]{1,255}$", Pattern.CASE_INSENSITIVE);
        final var matcher = pattern.matcher(filename);
        if (!matcher.matches()) {
            throw new BusinessLogicException(messageKey);
        }
        final var fileUUID = UUID.nameUUIDFromBytes(filename.getBytes());
        if (exists(tenantId, fileUUID)) {
            throw new BusinessLogicException("document.filename.duplicate");
        }
        return fileUUID;
    }

    public UUID convertToFileUUID(String uuid) throws BusinessLogicException {
        final var pattern = Pattern.compile("^([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-3[a-fA-F0-9]{3}-[8|9|aA|bB][a-fA-F0-9]{3}-[a-fA-F0-9]{12})$", Pattern.CASE_INSENSITIVE);
        final var matcher = pattern.matcher(uuid);
        if (!matcher.matches()) {
            throw new BusinessLogicException("files.path.invalid");
        }
        return UUID.fromString(uuid);
    }
}
