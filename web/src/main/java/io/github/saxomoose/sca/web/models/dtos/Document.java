package io.github.saxomoose.sca.web.models.dtos;

import java.util.regex.Pattern;
import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;

public class Document {
    private final String filename;

    public static Document of(String filename) throws IllegalArgumentException {
        final var messageKey = "document.filename.invalid";
        if (filename.isBlank()) {
            throw new IllegalArgumentException(new BusinessLogicException(messageKey));
        }
        final var pattern = Pattern.compile("^[a-zA-Z0-9]{1,255}", Pattern.CASE_INSENSITIVE);
        final var matcher = pattern.matcher(filename);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(new BusinessLogicException(messageKey));
        }
        return new Document(filename);
    }

    public Document(String filename) {
        this.filename = filename;
    }

    public String filename() {
        return filename;
    }
}
