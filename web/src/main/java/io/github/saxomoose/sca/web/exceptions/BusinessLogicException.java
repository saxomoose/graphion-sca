package io.github.saxomoose.sca.web.exceptions;

import io.github.saxomoose.sca.web.utils.Notification;

public class BusinessLogicException extends Exception {
    private final Notification notification;

    public BusinessLogicException(Notification notification)
    {
        this.notification = notification;
    }

    public Notification notification() {
        return notification;
    }
}
