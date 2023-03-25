package io.github.saxomoose.sca.web.dtos;

import io.github.saxomoose.sca.web.enums.NotificationType;

import java.io.Serializable;

public class Notification implements Serializable {
    private final NotificationType notificationType;
    private final String header;
    private final String body;

    public Notification(NotificationType notificationType, String header, String body) {
        this.notificationType = notificationType;
        this.header = header;
        this.body = body;
    }

    public NotificationType notificationType() { return notificationType; }
    public String header() { return header; }
    public String body() { return body; }
}
