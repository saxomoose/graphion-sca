package com.saxomoose.eid.web.dtos;

import com.saxomoose.eid.web.enums.ToastType;

public class ValidationError {
    private final String field;
    private final Toast toast;

    public ValidationError(String field, String code) {
        this.field = field;
        this.toast = new Toast(ToastType.ERROR, "validation.error", code);
    }

    public String getField() { return field; }

    public Toast getToast() { return toast; }
}