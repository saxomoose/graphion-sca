package io.github.saxomoose.sca.web.services;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import io.github.saxomoose.sca.web.exceptions.LogicException;
import io.github.saxomoose.sca.web.models.transfer.pki.AuthenticationPayload;
import io.github.saxomoose.sca.web.utils.Notification;

@Service
public class ValidationService {

    private final ClosingService closingService;
    private final DocumentService documentService;
    private final RedisService redisService;

    @Autowired
    public ValidationService(ClosingService closingService, DocumentService documentService, RedisService redisService)
    {
        this.closingService = closingService;
        this.documentService = documentService;
        this.redisService = redisService;
    }

    public String documentFilename(final String filename) throws LogicException
    {
        final Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]{1,255}$", Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(filename);
        if (filename.isBlank() || !matcher.matches()) {
            throw new LogicException(new Notification(Notification.Type.ERROR, "document.validate","document.filename.invalid"
            ));
        }
        return filename;
    }

    public void documentFileFormat(final String fileFormat) throws LogicException
    {
        if (!fileFormat.equals(MediaType.APPLICATION_PDF.toString())) {
            throw new LogicException(new Notification(Notification.Type.ERROR, "document.validate", "document.file.unsupported_file_format"));
        }
    }

    public void documentFileSize(final Long fileSize, final Long clientSideFileSize) throws LogicException {
        if (fileSize.equals(0L)) {
            throw new LogicException(new Notification(Notification.Type.ERROR, "document.validate", "document.file.empty"));
        } else if (!fileSize.equals(clientSideFileSize)) {
            throw new LogicException(new Notification(Notification.Type.ERROR, "document.validate", "document.file.size_mismatch"));
        }
    }

    public UUID uuid(final String uuid)
    {
        return UUID.fromString(uuid);
    }

    public void authenticationPayload(final AuthenticationPayload auth) throws LogicException 
    {
        try {
            // TODO: validate incoming JSON and email
        } catch(IllegalArgumentException exception) {
            throw new UnsupportedOperationException();
        }
    }

}
