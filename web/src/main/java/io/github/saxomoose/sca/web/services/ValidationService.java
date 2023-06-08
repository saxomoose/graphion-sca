package io.github.saxomoose.sca.web.services;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import io.github.saxomoose.sca.web.config.security.AuthenticationPayload;
import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;
import io.github.saxomoose.sca.web.utils.Notification;

@Service
public class ValidationService {

    private final DocumentService documentService;

    @Autowired
    public ValidationService(DocumentService documentService)
    {
        this.documentService = documentService;
    }

    public String documentFilename(final String filename) throws BusinessLogicException
    {
        final Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]{1,255}$", Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(filename);
        if (filename.isBlank() || !matcher.matches()) {
            throw new BusinessLogicException(new Notification(
                Notification.Type.ERROR,
                "document.validate",
                "document.filename.invalid"
            ));
        }
        return filename;
    }

    public void documentFileFormat(final String fileFormat) throws BusinessLogicException
    {
        if (!fileFormat.equals(MediaType.APPLICATION_PDF.toString())) {
            throw new BusinessLogicException(new Notification(
                Notification.Type.ERROR,
                "document.validate",
                "document.file.unsupported_file_format"));
        }
    }

    public void documentFileSize(final Long fileSize, final Long clientSideFileSize) throws BusinessLogicException {
        if (fileSize == 0) {
            throw new BusinessLogicException(new Notification(
                Notification.Type.ERROR,
                "document.validate",
                "document.file.empty"));
        } else if (fileSize != clientSideFileSize) {
            throw new BusinessLogicException(new Notification(
                Notification.Type.ERROR,
                "document.validate",
                "document.file.size_mismatch"));
        }
    }

    public UUID uuid(final String uuid)
    {
        return UUID.fromString(uuid);
    }

    public void authentication(final AuthenticationPayload auth) throws BusinessLogicException 
    {
        try {
            // TODO: validate incoming JSON and email
        } catch(IllegalArgumentException exception) {
            throw new UnsupportedOperationException();
        }
    }


}
