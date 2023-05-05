package io.github.saxomoose.sca.web.utils;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Notification implements Serializable {
    private final Type type;
    private final String header;
    private final String body;

    public Notification(Type type, String header, String body) 
    {
        this.type = type;
        this.header = header;
        this.body = body;
    }

    public Notification(Type type) 
    {
        this.type = type;
        this.header = null;
        this.body = null;
    }

    @JsonProperty("type")
    public Type type() 
    {
        return type;
    }

    @JsonProperty("header")
    public String header() 
    {
        return header;
    }

    @JsonProperty("body")
    public String body() 
    {
        return body;
    }

    public enum Type 
    {
        ERROR, INFORMATION, SUCCESS
    }
}
