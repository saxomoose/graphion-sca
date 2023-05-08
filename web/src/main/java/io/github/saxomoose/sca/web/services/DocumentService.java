package io.github.saxomoose.sca.web.services;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.saxomoose.sca.web.enums.Path;
import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;
import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.repositories.db.DocumentRepository;
import io.github.saxomoose.sca.web.utils.Tuple;

@Service
public class DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentService(DocumentRepository documentRepository)
    {
        this.documentRepository = documentRepository;
    }

    // Database
    public Set<Document> select(final Long tenantId)
    {
        final var documents = documentRepository.findAllByTenant(tenantId);
        return new HashSet<>(documents);
    }

    public Document select(final Long tenantId, final UUID fileUUID)
    {
        return documentRepository.select(tenantId, fileUUID);
    }

    public void create(final Long tenantId, final String filename, final UUID fileUUID)
    {
        final var document = Document.of(tenantId, filename, fileUUID);
        documentRepository.save(document);
    }

    /**
     * Validation
     */
    public Tuple<String, UUID> validateArgs(final Long tenantId, final String filename)
            throws BusinessLogicException
    {
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
        if (documentRepository.exists(tenantId, fileUUID)) {
            throw new BusinessLogicException("document.file_uuid.duplicate");
        }
        return Tuple.of(filename, fileUUID);
    }

    public UUID validateArgs(String uuid)
    {
        return UUID.fromString(uuid);
    }

    public String filePath(Path path, final UUID fileUUID)
    {
        return String.format("%s%s", path, fileUUID);
    }
}
