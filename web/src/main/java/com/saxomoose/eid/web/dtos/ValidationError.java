package com.saxomoose.eid.web.dtos;

import com.saxomoose.eid.web.enums.NotificationType;

public class ValidationError {
    private final String field;
    private final Notification notification;

    public ValidationError(String field, String code) {
        this.field = field;
        this.notification = new Notification(NotificationType.ERROR, "notification.error.validation", code);
    }

    public String getField() { return field; }

    public Notification getToast() { return notification; }
}