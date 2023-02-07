package com.saxomoose.eid.web.models;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "closing_signatory")
public class ClosingSignatory {
    @EmbeddedId
    private ClosingSignatoryKey id;

    @ManyToOne
    @MapsId("closingId")
    @JoinColumn(name = "closing_id")
    private Closing closing;

    @ManyToOne
    @MapsId("signatoryId")
    @JoinColumn(name = "signatory_id")
    private Signatory signatory;

    @Value("false")
    private Boolean status;

    public ClosingSignatoryKey getId() {
        return id;
    }

    public void setId(ClosingSignatoryKey id) {
        this.id = id;
    }

    public Closing getClosing() {
        return closing;
    }

    public void setClosing(Closing closing) {
        this.closing = closing;
    }

    public Signatory getSignatory() {
        return signatory;
    }

    public void setSignatory(Signatory signatory) {
        this.signatory = signatory;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
