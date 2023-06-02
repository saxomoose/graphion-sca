package io.github.saxomoose.sca.web.enums;

public enum Path {
    BELGIUM_CERTS("certs/belgium"),
    FILES("files"),
    OJ_CERTS("certs/oj"),
    SIGNED("signed"),
    WORKBENCH("workbench");

    private final String key;

    Path(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
