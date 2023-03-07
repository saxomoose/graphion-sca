package io.github.saxomoose.sca.web.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Document {
    @NotBlank
    @Size(max = 255)
    private String friendlyFilename;

    private String path;

    public String getFriendlyFilename() { return friendlyFilename; }
    public String getPath() { return path; }

    public void setFriendlyFilename(String friendlyFilename) { this.friendlyFilename = friendlyFilename; }
    public void setPath(String path) { this.path = path; }
}
