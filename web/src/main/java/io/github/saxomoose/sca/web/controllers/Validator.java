package io.github.saxomoose.sca.web.controllers;

import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;

import java.util.regex.Pattern;

public class Validator {
    public static void validateFilename (String filename) throws BusinessLogicException {
        final var messageKey = "document.filename.invalid";
        if (filename.isBlank()) {
            throw new BusinessLogicException(messageKey);
        }
        final var pattern = Pattern.compile("^[a-zA-Z0-9_-]{1,255}", Pattern.CASE_INSENSITIVE);
        final var matcher = pattern.matcher(filename);
        if (!matcher.matches()) {
            throw new BusinessLogicException(messageKey);
        }
    }
}
