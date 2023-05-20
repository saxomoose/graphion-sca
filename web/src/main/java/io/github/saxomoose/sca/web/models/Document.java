package io.github.saxomoose.sca.web.models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table(schema = "sca")
public class Document {
    private final Long tenant;
    @Id
    private final Long id;
    private final String filename;
    @ReadOnlyProperty
    private final UUID fileUUID;

    @Version
    private Long version;
    @CreatedBy
    private Long createdBy;
    @LastModifiedBy
    private Long lastModifiedBy;
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;


    public static Document overloadOf(Long id, String filename)
    {
        return new Document(null, id, filename, null);
    }

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
