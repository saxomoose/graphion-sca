package io.github.saxomoose.sca.web.enums;

public enum Path {
    CERTS("certs"),
    FILES("files"),
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
