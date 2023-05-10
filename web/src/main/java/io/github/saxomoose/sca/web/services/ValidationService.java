package io.github.saxomoose.sca.web.services;

import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;
import io.github.saxomoose.sca.web.repositories.db.DocumentRepository;
import io.github.saxomoose.sca.web.utils.Tuple;

@Service
public class ValidationService {

    private final DocumentRepository documentRepository;

    @Autowired
    public ValidationService(DocumentRepository documentRepository)
    {
        this.documentRepository = documentRepository;
    }

    public Tuple<String, UUID> documentArgs(final Long tenantId, final String filename)
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

    public UUID uuid(String uuid)
    {
        return UUID.fromString(uuid);
    }


    
}
