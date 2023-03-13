package io.github.saxomoose.sca.web.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class Document {
    @NotBlank
    @Size(max = 255)
    private String filename;
    @NotNull
    private MultipartFile file;

    public String getFilename() { return filename; }
    public MultipartFile getFile() { return file; }

    public void setFilename(String filename) { this.filename = filename; }
    public void setFile(MultipartFile file) { this.file = file; }


}
