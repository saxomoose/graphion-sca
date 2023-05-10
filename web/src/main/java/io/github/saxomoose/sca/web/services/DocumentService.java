package io.github.saxomoose.sca.web.services;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.github.saxomoose.sca.web.enums.Path;
import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.repositories.db.DocumentRepository;

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

    public String filePath(Path path, final UUID fileUUID)
    {
        return String.format("%s%s", path, fileUUID);
    }
}
