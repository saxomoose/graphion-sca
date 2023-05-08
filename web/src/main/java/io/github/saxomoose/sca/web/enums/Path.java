package io.github.saxomoose.sca.web.enums;

public enum Path {
    UPLOADS("/uploads/");

    private final String key;

    Path(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
