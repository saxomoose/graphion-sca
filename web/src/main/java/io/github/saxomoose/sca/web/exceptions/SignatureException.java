package io.github.saxomoose.sca.web.exceptions;

import io.github.saxomoose.sca.web.utils.Notification;

public class SignatureException extends BusinessLogicException {

    public SignatureException(Notification notification) {
        super(notification);
    }    
}
