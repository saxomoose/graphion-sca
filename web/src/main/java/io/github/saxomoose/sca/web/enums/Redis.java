package io.github.saxomoose.sca.web.enums;

public enum Redis {
    JOBS_KEY("graphionscaweb:jobs"),
    STANDARD_CONSUMER_GROUP("standardgroup"),
    STANDARD_CONSUMER("standardconsumer"),
    WORKBENCH_KEY("graphionscaweb:workbench");

    private final String key;

    Redis(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
