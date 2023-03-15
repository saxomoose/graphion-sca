package io.github.saxomoose.sca.web.services;

import io.github.saxomoose.sca.web.exceptions.ObjectStorageException;
import io.minio.*;
import io.minio.errors.MinioException;
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

//    private byte[] getFileAsBytes(String bucket, String name) {
//        byte[] outBytes = null;
//        InputStream inputStream = null;
//        try {
//            final var statObjectResponse = statObject(name);
//            final var size = statObjectResponse.size();
//            inputStream = getFileAsStream()
//        }
//    }

    public StatObjectResponse statObject(final String bucket, final String path) throws ObjectStorageException {
        try {
            return Objects.requireNonNull(minioClient.statObject(StatObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(path)
                    .build())
            );
        } catch (InvalidKeyException | IOException | MinioException | NoSuchAlgorithmException | NullPointerException e) {
            logger.info(e.getMessage());
            throw new ObjectStorageException("TODO");
        }
    }

    public InputStream getObjectAsStream(final String bucket, final String path) throws ObjectStorageException {
        // Handle exception if file does not exist.
        try {
            return Objects.requireNonNull(minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build())
            );

        } catch (InvalidKeyException | IOException | MinioException | NoSuchAlgorithmException | NullPointerException e) {
            logger.info(e.getMessage());
            throw new ObjectStorageException("TODO");
        }
    }

    public void storeFile(final String bucket, final String path, Long clientSideFileSize, final InputStream inputStream) throws ObjectStorageException {
        @SuppressWarnings("DataFlowIssue")
        final var setBucket = Objects.requireNonNullElse(bucket, environment.getProperty("objectstorage.defaultbucket"));
        configureBucket(setBucket);
        storeFile(setBucket, path, inputStream);
        if (statObject(bucket, path).size() != clientSideFileSize) {
            throw new ObjectStorageException("TODO");
        }
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
    private void storeFile(final String bucket, final String path, final InputStream inputStream) throws ObjectStorageException {
        try {
            Objects.requireNonNull(minioClient.putObject(PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .stream(inputStream, inputStream.available(), -1)
//                    .contentType(MediaType.APPLICATION_PDF.toString())
                            .build())
            );
        } catch (InvalidKeyException | IOException | MinioException | NoSuchAlgorithmException | NullPointerException e) {
            logger.info(e.getMessage());
            throw new ObjectStorageException("TODO");
        }
    }
}
