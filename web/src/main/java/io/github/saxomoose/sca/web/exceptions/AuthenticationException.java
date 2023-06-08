package io.github.saxomoose.sca.web.exceptions;

import io.github.saxomoose.sca.web.utils.Notification;

public class AuthenticationException extends BusinessLogicException {

    public AuthenticationException(Notification notification) {
        super(notification);
    }
}
