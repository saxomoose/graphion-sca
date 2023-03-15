package io.github.saxomoose.sca.web.exceptions;

public class ObjectStorageException extends Exception {
    private final String messageKey;

    public ObjectStorageException(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() { return messageKey; }
}
