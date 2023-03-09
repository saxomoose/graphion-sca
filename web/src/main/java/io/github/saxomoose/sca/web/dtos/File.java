package io.github.saxomoose.sca.web.dtos;

import com.google.common.net.MediaType;
import io.github.saxomoose.sca.web.exceptions.NullOrEmptyFileException;
import io.github.saxomoose.sca.web.exceptions.UnsupportedFileFormatException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

public class File {
    private final MediaType mediaType;
    private final byte[] contentBytes;

    public static File of(final MultipartFile file) throws IOException, IllegalArgumentException {
        MediaType mediaType;
        try {
            mediaType = MediaType.parse(Objects.requireNonNull(file.getContentType()));
            if (!mediaType.equals(MediaType.PDF)) {
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
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new IllegalArgumentException(new NullOrEmptyFileException());
        }

        return new File(mediaType, bytes);
    }

    public File(MediaType mediaType, byte[] contentBytes) {
        this.mediaType = mediaType;
        this.contentBytes = contentBytes;
    }

    public MediaType getMediaType() { return mediaType; }
    public byte[] getContentBytes() { return contentBytes; }
}
