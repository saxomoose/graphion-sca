package io.github.saxomoose.sca.web.services;

import io.github.saxomoose.sca.web.dtos.File;
import io.github.saxomoose.sca.web.exceptions.NullOrEmptyFileException;
import io.github.saxomoose.sca.web.exceptions.UnsupportedFileFormatException;
import io.github.saxomoose.sca.web.models.Tenant;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

// Singleton by default.
@Service
public class ObjectStorageService {
    private static final Logger logger = LoggerFactory.getLogger(ObjectStorageService.class);

    private final MinioClient minioClient;
    private final Environment environment;

    @Autowired
    public ObjectStorageService(
            MinioClient minioClient,
            Environment environment) {
        this.minioClient = minioClient;
        this.environment = environment;
    }

    public File storeFile(String filename, final MultipartFile rawFile, final Tenant tenant) throws IllegalArgumentException {
        final var name = UUID.nameUUIDFromBytes(filename.getBytes());
        final var file = parseFile(rawFile);
        Objects.requireNonNull(file);
        final var bucket = configureBucket(tenant.getBucket());
        storeFile(bucket, name, file);
        return file;
    }

    private File parseFile(final MultipartFile rawFile) throws IllegalArgumentException {
        try {
            return File.of(rawFile);
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

    private String configureBucket(String bucket) {
        bucket = Objects.requireNonNullElse(bucket, environment.getProperty("objectstorage.defaultbucket"));
        try {
            final var bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
            return bucket;
        } catch (InvalidKeyException | IOException | MinioException | NoSuchAlgorithmException e) {
            logger.info(e.getMessage());
        }
        // Not run.
        return null;
    }

    private void storeFile(final String bucket, UUID name, final File file) {
    try {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object(name.toString())
                .stream(new ByteArrayInputStream(
                        file.getContentBytes()),
                        file.getContentBytes().length,
                        -1)
                .build()
        );
    } catch (InvalidKeyException | IOException | MinioException | NoSuchAlgorithmException e) {
        logger.info(e.getMessage());
    }
}

}
