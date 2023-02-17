package com.saxomoose.eid.web.dtos;

import com.saxomoose.eid.web.enums.NotificationType;

public class Notification {
    private final NotificationType notificationType;
    private final String header;
    private final String body;

    public Notification(NotificationType notificationType, String header, String body) {
        this.notificationType = notificationType;
        this.header = header;
        this.body = body;
    }

    public NotificationType getToastType() { return notificationType; }

    public String getHeader() { return header; }

    public String getBody() { return body; }
}
