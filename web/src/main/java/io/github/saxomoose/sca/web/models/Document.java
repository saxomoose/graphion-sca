package io.github.saxomoose.sca.web.models;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Table(schema = "sca")
public class Document {
    @Id
    private final Long id;
    private final String filename;
    private final String path;

    private final Long tenant;

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

    public static Document of(String filename, String path, Long tenant) {
        return new Document(null, filename, path, tenant);
    }

    public Document(Long id, String filename, String path, Long tenant) {
        this.id = id;
        this.filename = filename;
        this.path = path;
        this.tenant = tenant;
    }

    public Long id() {
        return id;
    }
    public String filename() { return filename; }
    public String path() { return path; }
    public Long tenant() { return tenant; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(id, document.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
