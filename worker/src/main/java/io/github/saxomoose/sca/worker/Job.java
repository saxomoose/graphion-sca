package io.github.saxomoose.sca.worker;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Job {
    private Long tenantId;
    private Status status;

    public Job() {}
    public Job(Long tenantId) {
        this.tenantId = tenantId;
        this.status = Status.IN_PROGRESS;
    }

    @JsonProperty("tenant_id")
    public Long tenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    @JsonProperty("job_status")
    public Status status() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        FAILED,
        IN_PROGRESS,
        SUCCESSFUL
    }
    public enum Type {
        UPLOAD
    }
}
