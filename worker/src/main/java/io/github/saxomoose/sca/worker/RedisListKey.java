package io.github.saxomoose.sca.worker;

public enum RedisListKey {
    JOBS("graphionscaweb:jobs");

    private final String key;

    RedisListKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
