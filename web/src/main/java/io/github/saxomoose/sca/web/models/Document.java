package io.github.saxomoose.sca.web.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table(schema = "sca")
public class Document {
    @Id
    private final Long id;
    private final String filename;
    private final String path;

    private final Long tenant;

    @Version
    private Long version;

    public static Document of(String filename, String path, Long tenant) {
        return new Document(null, filename, path, tenant);
    }

    public Document(Long id, String filename, String path, Long tenant) {
        this.id = id;
        this.filename = filename;
        this.path = path;
        this.tenant = tenant;
    }

    public Long getId() {
        return id;
    }
    public String getFilename() { return filename; }
    public String getPath() { return path; }
    public Long getTenant() { return tenant; }
}
