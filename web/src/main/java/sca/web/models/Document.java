package sca.web.models;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import sca.web.enums.SupportedMediaType;

import java.time.Instant;

public class Document {
    @Id
    private Long id;

    @NotNull
    @CreatedBy
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @NotNull
    private String filename;

    @NotNull
    private String path;

    @NotNull
    private SupportedMediaType mediaType = SupportedMediaType.PDF;

    // Minio attributes: uuid, versionuuid

    @CreatedDate
    private Instant createdTimestamp;

    @LastModifiedDate
    protected Instant lastModifiedTimestamp;

    public Document() { }

    public Document(Tenant tenant, String fileName) {
        this.tenant = tenant;
        this.filename = fileName;
    }

    public Long getId() {
        return id;
    }

    public String getFilename() { return filename; }

    public void setFilename(String filename) { this.filename = filename; }

    public Instant getCreatedTimestamp() { return createdTimestamp; }

    public void setCreatedTimestamp(Instant createdTimestamp) { this.createdTimestamp = createdTimestamp; }

    public Instant getLastModifiedTimestamp() { return lastModifiedTimestamp; }

    public void setLastModifiedTimestamp(Instant modifiedTimestamp) { this.lastModifiedTimestamp = modifiedTimestamp; }
}
