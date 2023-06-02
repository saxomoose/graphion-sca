package io.github.saxomoose.sca.web.enums;

public enum Path {
    BELGIUM_CERTS("certs/belgium"),
    DSS_TLCACHE("dss/tlcache"),
    DSS_WORKBENCH("dss/workbench"),
    FILES("files"),
    OJ_CERTS("certs/oj"),
    SIGNED("signed");

    private final String key;

    Path(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
