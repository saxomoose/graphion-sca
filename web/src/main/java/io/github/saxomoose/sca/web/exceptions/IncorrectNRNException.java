package io.github.saxomoose.sca.web.exceptions;

import org.springframework.security.core.AuthenticationException;

public class IncorrectNRNException extends AuthenticationException {

    public IncorrectNRNException(String msg) {
        super(msg);
    }
    
}
