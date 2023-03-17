package io.github.saxomoose.sca.web.services;

import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class ValidationService {
    private final DocumentService documentService;

    @Autowired
    public ValidationService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public UUID validateFilename (Long tenantId, String filename) throws BusinessLogicException {
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
        if (documentService.exists(tenantId, fileUUID)) {
            throw new BusinessLogicException("document.filename.duplicate");
        }
        return fileUUID;
    }

    public UUID validateUUID (String uuid) throws BusinessLogicException {
        final var pattern = Pattern.compile("^([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-3[a-fA-F0-9]{3}-[8|9|aA|bB][a-fA-F0-9]{3}-[a-fA-F0-9]{12})$", Pattern.CASE_INSENSITIVE);
        final var matcher = pattern.matcher(uuid);
        if (!matcher.matches()) {
            throw new BusinessLogicException("files.path.invalid");
        }
        return UUID.fromString(uuid);
    }
}
