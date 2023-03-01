package sca.web.dtos;

import sca.web.enums.NotificationType;

public class ValidationError {
    private final String field;
    private final Notification notification;

    public ValidationError(String field, String code) {
        this.field = field;
        this.notification = new Notification(NotificationType.ERROR, "notification.error.validation", code);
    }

    public String getField() { return field; }

    public Notification getNotification() { return notification; }
}