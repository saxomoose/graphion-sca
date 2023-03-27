package io.github.saxomoose.sca.web.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class Job {
    private UUID uuid;

    public Job() {}
    public Job(UUID uuid) {
        this.uuid = uuid;
    }

    @JsonProperty("uuid")
    public UUID uuid() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }
}
