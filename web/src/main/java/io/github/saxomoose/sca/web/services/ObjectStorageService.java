package io.github.saxomoose.sca.web.services;

import io.github.saxomoose.sca.web.exceptions.ObjectStorageException;
import io.github.saxomoose.sca.web.models.Tenant;
import io.minio.*;
import io.minio.errors.MinioException;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

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

    public InputStream getObjectAsStream(String path) throws ObjectStorageException {
        final var bucket = setBucket();
        return getObjectAsStream(bucket, path);
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

    private StatObjectResponse statObject(final String bucket, final UUID name) throws ObjectStorageException {
        try {
            return Objects.requireNonNull(minioClient.statObject(StatObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(name.toString())
                    .build())
            );
        } catch (InvalidKeyException | IOException | MinioException | NoSuchAlgorithmException | NullPointerException e) {
            logger.info(e.getMessage());
            throw new ObjectStorageException();
        }
    }

    private InputStream getObjectAsStream(final String bucket, String path) throws ObjectStorageException {
        // Handle exception if file does not exist.
        try {
            return Objects.requireNonNull(minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build())
            );

        } catch (InvalidKeyException | IOException | MinioException | NoSuchAlgorithmException | NullPointerException e) {
            logger.info(e.getMessage());
            throw new ObjectStorageException();
        }
    }

    public ObjectWriteResponse storeFile(final String filename, final InputStream inputStream) throws IllegalArgumentException, ObjectStorageException {
        final var name = UUID.nameUUIDFromBytes(filename.getBytes());
        final var bucket = setBucket();
        configureBucket(bucket);
        final var objectWriteResponse = storeFile(bucket, name, inputStream);
        return objectWriteResponse;
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
    private ObjectWriteResponse storeFile(final String bucket, final UUID name, final InputStream inputStream) throws ObjectStorageException {
        try {
            return Objects.requireNonNull(minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(String.format("/documents/%s", name))
                    .stream(inputStream, inputStream.available(), -1)
//                    .contentType(MediaType.APPLICATION_PDF.toString())
                    .build())
            );
        } catch (InvalidKeyException | IOException | MinioException | NoSuchAlgorithmException |
                 NullPointerException e) {
            logger.info(e.getMessage());
            throw new ObjectStorageException();
        }
    }
}
