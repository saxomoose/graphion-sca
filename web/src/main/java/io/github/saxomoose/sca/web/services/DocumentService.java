package io.github.saxomoose.sca.web.services;

import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;
import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.repositories.db.DocumentRepository;
import io.github.saxomoose.sca.web.utils.Tuple;
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

    @Autowired
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    // Database
    public Set<Document> read(final Long tenantId) {
        final var documents = documentRepository.findAllByTenant(tenantId);
        return new HashSet<>(documents);
    }

    public Document read(final Long tenantId, final String path) {
        return documentRepository.select(tenantId, path);
    }

    public void create(final Long tenantId, final String filename, final String path){
        final var document = Document.of(tenantId, filename, path);
        documentRepository.save(document);
    }

    private Boolean exists(final Long tenantId, final String path) {
        return documentRepository.exists(tenantId, path);
    }

    /**
     * Validation
      */
    public Tuple<String, String> converToValidArgs(final Long tenantId, final String filename) throws BusinessLogicException {
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
        final var path = generatePath(fileUUID);
        if (exists(tenantId, path)) {
            throw new BusinessLogicException("document.filename.duplicate");
        }
        return Tuple.of(filename, path);
    }

    public String convertToValidArgs(String uuid) throws BusinessLogicException {
        final var pattern = Pattern.compile("^([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-3[a-fA-F0-9]{3}-[8|9|aA|bB][a-fA-F0-9]{3}-[a-fA-F0-9]{12})$", Pattern.CASE_INSENSITIVE);
        final var matcher = pattern.matcher(uuid);
        if (!matcher.matches()) {
            throw new BusinessLogicException("files.path.invalid");
        }
        return generatePath(UUID.fromString(uuid));
    }

    private String generatePath(final UUID fileUUID) {
        return String.format("/files/%s", fileUUID);
    }
}
