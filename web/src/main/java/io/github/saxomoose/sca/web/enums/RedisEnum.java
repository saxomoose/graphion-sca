package io.github.saxomoose.sca.web.enums;

public enum RedisEnum {
    JOB_KEY("graphionscaweb:job"),
    STANDARD_CONSUMER_GROUP("standardgroup"),
    STANDARD_CONSUMER("standardconsumer"),
    USER_PINCODE_KEY("graphionscaweb:user:pincode"),
    WORKBENCH_KEY("graphionscaweb:workbench");

    private final String key;

    RedisEnum(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
