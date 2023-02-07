package com.saxomoose.eid.web.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ClosingSignatoryKey implements Serializable {
    @Column(name = "closing_id")
    private Long closingId;

    @Column(name = "signatory_id")
    private Long signatoryId;

    public Long getClosingId() {
        return closingId;
    }

    public Long getSignatoryId() {
        return signatoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClosingSignatoryKey that = (ClosingSignatoryKey) o;
        return getClosingId().equals(that.getClosingId()) && getSignatoryId().equals(that.getSignatoryId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClosingId(), getSignatoryId());
    }
}
