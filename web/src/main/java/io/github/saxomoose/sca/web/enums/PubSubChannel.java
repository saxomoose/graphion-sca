package io.github.saxomoose.sca.web.enums;

public enum PubSubChannel {
    JOBS("graphionscaweb:jobs");

    private final String channel;

    PubSubChannel(String channel) {
        this.channel = channel;
    }

    public String channel() {
        return channel;
    }
}
