package io.github.saxomoose.sca.web.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.saxomoose.sca.web.enums.JobStatus;

public class Job {
    private Long tenantId;
    private String id;
    private JobStatus jobStatus;

    public Job() {}
    public Job(Long tenantId) {
        this.tenantId = tenantId;
        this.jobStatus = JobStatus.IN_PROGRESS;
    }

    @JsonProperty("tenant_id")
    public Long tenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    @JsonProperty("id")
    public String id() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("job_status")
    public JobStatus jobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }
}
