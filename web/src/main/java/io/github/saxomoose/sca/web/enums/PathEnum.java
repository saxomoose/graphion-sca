package io.github.saxomoose.sca.web.enums;

public enum PathEnum {
    CERTS_DSS_BE("certs/dss/be"),
    CERTS_DSS_EU("certs/dss/eu"),
    CERTS_DSS_TIMESTAMP("certs/css/timestamp"),
    CERTS_WEBEID("certs/webeid"),
    CLOSING_DOCUMENTS("closing-documents"),
    CLOSINGS("closings"),
    DSS_TRUST_LISTS("dss/trust-lists"),
    DSS_WORKBENCH("dss/workbench"),
    DSS_WORKBENCH_TMP("dss/workbench/tmp"),
    SIGNED("signed"),
    TENANTS("tenants");

    private final String key;

    PathEnum(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
