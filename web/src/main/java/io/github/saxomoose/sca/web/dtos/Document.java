package io.github.saxomoose.sca.web.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class Document {
    @NotBlank
    @Size(max = 255)
    private String friendlyFilename;
    @NotNull
    private MultipartFile file;

    public String getFriendlyFilename() { return friendlyFilename; }
    public MultipartFile getFile() { return file; }

    public void setFriendlyFilename(String friendlyFilename) { this.friendlyFilename = friendlyFilename; }
    public void setFile(MultipartFile file) { this.file = file; }


}
