package io.github.saxomoose.sca.web.enums;

public enum Path {
    CERTS_BELGIUM("certs/belgium"),
    DSS_TRUST_LISTS("dss/trust-lists"),
    DSS_WORKBENCH("dss/workbench"),
    CLOSING_DOCUMENTS("closing-documents"),
    CERTS_OJ("certs/oj"),
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
