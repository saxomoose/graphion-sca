package io.github.saxomoose.sca.web.models.dtos;

public class FileInfo {
    private final Long size;
    private final String versionId;

    public FileInfo(Long size, String versionId) {
        this.size = size;
        this.versionId = versionId;
    }
}
