package io.github.saxomoose.sca.web.models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Document {
    private final Long tenant;
    
    private final Long id;
    private final String filename;
    private final UUID fileUUID;

    private Long createdBy;
    private Long lastUpdatedBy;
    private LocalDateTime createdTimestamp;
    private LocalDateTime lastUpdatedTimestamp;

    public static Document of(Long tenantId, String filename) {
        return new Document(tenantId, null, filename, null);
    }

    public Document(Long tenant, Long id, String filename, UUID fileUUID) {
        this.tenant = tenant;
        this.id = id;
        this.filename = filename;
        this.fileUUID = fileUUID;
    }

    public Long tenantId() {
        return tenant;
    }

    public Long id() {
        return id;
    }

    public String filename() {
        return filename;
    }

    public UUID fileUUID() {
        return fileUUID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Document document = (Document) o;
        return Objects.equals(id, document.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
