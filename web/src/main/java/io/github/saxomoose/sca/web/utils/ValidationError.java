package io.github.saxomoose.sca.web.utils;

public class ValidationError {
    private final String field;
    private final Notification notification;

    public ValidationError(String field, String code) {
        this.field = field;
        this.notification = new Notification(
                Notification.Type.ERROR,
                "notification.error.validation",
                code);
    }

    public String field() {
        return field;
    }

    public Notification notification() {
        return notification;
    }
}
