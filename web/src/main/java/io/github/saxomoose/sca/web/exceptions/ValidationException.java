package io.github.saxomoose.sca.web.exceptions;

import org.springframework.validation.BindingResult;

public class ValidationException extends Exception {
    final BindingResult bindingResult;

    public ValidationException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() { return bindingResult; }
}
