package io.github.saxomoose.sca.web.exceptions;

import io.github.saxomoose.sca.web.utils.Notification;

public class LogicException extends Exception {
    private final Notification notification;

    public LogicException(Notification notification)
    {
        this.notification = notification;
    }

    public Notification notification() {
        return notification;
    }
}
