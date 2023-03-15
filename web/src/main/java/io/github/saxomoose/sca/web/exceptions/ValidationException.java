package io.github.saxomoose.sca.web.exceptions;

public class ValidationException extends Exception {
    private final String messageKey;

    public ValidationException(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() { return this.messageKey; }
}
