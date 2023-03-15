package io.github.saxomoose.sca.web.exceptions;

public class BusinessLogicException extends Exception {
    private final String messageKey;

    public BusinessLogicException(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() { return this.messageKey; }
}
