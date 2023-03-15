package io.github.saxomoose.sca.web.dtos;

import io.github.saxomoose.sca.web.exceptions.ValidationException;

import java.util.regex.Pattern;

public class Document {
    private final String filename;

    public static Document of (String filename) throws IllegalArgumentException {
        final var messageKey = "document.filename.invalid";
        if (filename.isBlank()) {
            throw new IllegalArgumentException(new ValidationException(messageKey));
        }
        final var pattern = Pattern.compile("^[a-zA-Z0-9]{1,255}", Pattern.CASE_INSENSITIVE);
        final var matcher = pattern.matcher(filename);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(new ValidationException(messageKey));
        }
        return new Document(filename);
    }

    public Document(String filename) {
        this.filename = filename;
    }

    public String getFilename() { return filename; }
}
