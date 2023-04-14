package io.github.saxomoose.sca.web.utils;

import java.io.Serializable;

public class Notification implements Serializable {
    private final Type type;
    private final String header;
    private final String body;

    public Notification(Type type, String header, String body) {
        this.type = type;
        this.header = header;
        this.body = body;
    }

    public Type type() { return type; }
    public String header() { return header; }
    public String body() { return body; }

    public enum Type {
        ERROR,
        INFORMATION,
        SUCCESS
    }
}
