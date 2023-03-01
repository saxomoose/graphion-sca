package sca.web.models;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.time.Instant;

public class ClosingSignatory {
    @Id
    private Long id;

    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "closing_id")
    private Closing closing;

    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "signatory_id")
    private Signatory signatory;

    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "document_id")
    private Document document;

    private Instant signatureTimestamp;

    public Instant getSignatureTimestamp() { return signatureTimestamp; }

    public void setSignatureTimestamp(Instant timestamp) { this.signatureTimestamp = timestamp; }
}
