package io.github.saxomoose.sca.web.enums;

public enum RedisKey {
    JOBS("graphionscaweb:jobs");

    private final String key;

    RedisKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
