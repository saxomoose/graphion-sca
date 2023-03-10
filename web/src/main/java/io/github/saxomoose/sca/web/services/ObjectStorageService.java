package io.github.saxomoose.sca.web.services;

import io.github.saxomoose.sca.web.dtos.FileToCreate;
import io.github.saxomoose.sca.web.exceptions.NullOrEmptyFileException;
import io.github.saxomoose.sca.web.exceptions.UnsupportedFileFormatException;
import io.github.saxomoose.sca.web.models.Tenant;
import io.minio.*;
import io.minio.errors.MinioException;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

// Singleton by default.
@Service
public class ObjectStorageService {
    private static final Logger logger = LoggerFactory.getLogger(ObjectStorageService.class);

    private final HttpSession httpSession;
    private final MinioClient minioClient;
    private final Environment environment;

    @Autowired
    public ObjectStorageService(
            HttpSession httpSession, MinioClient minioClient,
            Environment environment) {
        this.httpSession = httpSession;
        this.minioClient = minioClient;
        this.environment = environment;
    }

    public InputStreamResource getFileAsResource(String name) {
        final var bucket = setBucket();
        try {
            return new InputStreamResource(Objects.requireNonNull(getFileAsStream(bucket, name)));
        } catch (NullPointerException e) {
            logger.info(e.getMessage());
        }
        return null;
    }

//    private byte[] getFileAsBytes(String bucket, String name) {
//        byte[] outBytes = null;
//        InputStream inputStream = null;
//        try {
//            final var statObjectResponse = statObject(name);
//            final var size = statObjectResponse.size();
//            inputStream = getFileAsStream()
//        }
//    }

    private StatObjectResponse statObject(String bucket, String name) {
        try {
            return Objects.requireNonNull(minioClient.statObject(StatObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(name)
                    .build())
            );
        } catch (InvalidKeyException | IOException | MinioException | NoSuchAlgorithmException | NullPointerException e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    private InputStream getFileAsStream(String bucket, String name) {
        // Handle exception if file does not exist.
        try {
            return Objects.requireNonNull(minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(name).build())
            );

        } catch (InvalidKeyException | IOException | MinioException | NoSuchAlgorithmException | NullPointerException e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    public ObjectWriteResponse storeFile(final String filename, final MultipartFile rawFile) throws IllegalArgumentException {
        final var name = UUID.nameUUIDFromBytes(filename.getBytes());
        final var file = parseFile(rawFile);
        Objects.requireNonNull(file);
        final var bucket = setBucket();
        configureBucket(bucket);
        final var createFile = storeFile(bucket, name, file);
        return createFile;
    }

    private FileToCreate parseFile(final MultipartFile rawFile) throws IllegalArgumentException {
        try {
            return FileToCreate.of(rawFile);
        } catch (IOException e) {
            logger.info(e.getMessage());
        } catch (IllegalArgumentException e) {
            if (e.getCause() instanceof UnsupportedFileFormatException) {
                logger.info("Document not created. Unsupported file format.");
                throw e;
            } else if (e.getCause() instanceof NullOrEmptyFileException) {
                logger.info("Document not created. Empty document.");
                throw e;
            }
        }
        // Not run.
        return null;
    }

    private String setBucket() {
        var bucket = ((Tenant) httpSession.getAttribute("tenant")).getBucket();
        bucket = Objects.requireNonNullElse(bucket, environment.getProperty("objectstorage.defaultbucket"));
        return bucket;
    }

    // Should only create 1 bucket per tenant.
    private void configureBucket(String bucket) {
        try {
            final var bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                        .bucket(bucket)
                        .build()
            );
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucket)
                        .build()
                );
            }
        } catch (InvalidKeyException | IOException | MinioException | NoSuchAlgorithmException e) {
            logger.info(e.getMessage());
        }
    }

    // WriteResponseObject contains versionId.
    private ObjectWriteResponse storeFile(final String bucket, final UUID name, final FileToCreate fileToCreate) {
    try {
        return Objects.requireNonNull(minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object(String.format("/documents/%s", name.toString()))
                .stream(new ByteArrayInputStream(
                        fileToCreate.getContentBytes()),
                        fileToCreate.getContentBytes().length,
                        -1)
                .contentType(fileToCreate.getMediaType().toString())
                .build())
        );
    } catch (InvalidKeyException | IOException | MinioException | NoSuchAlgorithmException | NullPointerException e) {
        logger.info(e.getMessage());
    }
        return null;
    }

}
