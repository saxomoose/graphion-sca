package io.github.saxomoose.sca.web.utils;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Job {
    private Long tenantId;
    private Type type;
    private Status status;
    private String message;
    private Map<String, String> extras;

    public Job()
    {
    }

    public Job(Long tenantId, Type type, String message, Map<String, String> extras)
    {
        this.tenantId = tenantId;
        this.type = type;
        this.status = Status.IN_PROGRESS;
        this.message = message;
        this.extras = extras;
    }

    @JsonProperty("tenant_id")
    public Long tenantId()
    {
        return tenantId;
    }

    @JsonProperty("status")
    public Status status()
    {
        return status;
    }

    @JsonProperty("type")
    public Type type()
    {
        return type;
    }

    @JsonProperty("message")
    public String message()
    {
        return message;
    }

    @JsonProperty("extras")
    public Map<String, String> extras()
    {
        return extras;
    }

    public void setTenantId(Long tenantId)
    {
        this.tenantId = tenantId;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    public enum Status {
        FAILED, IN_PROGRESS, SUCCESSFUL
    }

    public enum Type {
        UPLOAD
    }
}
