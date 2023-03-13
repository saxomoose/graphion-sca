package io.github.saxomoose.sca.web.dtos;

import org.springframework.http.MediaType;
import io.github.saxomoose.sca.web.exceptions.InvalidFileException;
import io.github.saxomoose.sca.web.exceptions.UnsupportedFileFormatException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

public class FileToCreate {
    private final MediaType mediaType;
    private final byte[] contentBytes;

    public static FileToCreate of(final MultipartFile file) throws IllegalArgumentException {
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(Objects.requireNonNull(file.getContentType()));
            if (!mediaType.equals(MediaType.APPLICATION_PDF)) {
                throw new IllegalArgumentException();
            }
        } catch (NullPointerException | IllegalArgumentException e){
            throw new IllegalArgumentException(new UnsupportedFileFormatException());
        }
        byte[] bytes;
        try {
            bytes = Objects.requireNonNull(file.getBytes());
            if (bytes.length == 0) {
                throw new IllegalArgumentException();
            }
        } catch (NullPointerException | IllegalArgumentException | IOException e) {
            throw new IllegalArgumentException(new InvalidFileException());
        }

        return new FileToCreate(mediaType, bytes);
    }

    public FileToCreate(MediaType mediaType, byte[] contentBytes) {
        this.mediaType = mediaType;
        this.contentBytes = contentBytes;
    }

    public MediaType getMediaType() { return mediaType; }
    public byte[] getContentBytes() { return contentBytes; }
}
