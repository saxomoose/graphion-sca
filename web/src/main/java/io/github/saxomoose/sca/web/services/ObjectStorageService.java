package io.github.saxomoose.sca.web.services;

import io.github.saxomoose.sca.web.exceptions.ObjectStorageException;
import io.minio.*;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

// Singleton by default.
@Service
public class ObjectStorageService {
    private static final Logger logger = LoggerFactory.getLogger(ObjectStorageService.class);

    private final MinioClient minioClient;

    @Autowired
    public ObjectStorageService(
            MinioClient minioClient
    ) {
        this.minioClient = minioClient;
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

    public StatObjectResponse stat(final String bucket, final String path) throws ObjectStorageException {
        try {
            return minioClient.statObject(StatObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(path)
                    .build()
            );
        } catch (InvalidKeyException | IOException | MinioException | NoSuchAlgorithmException e) {
            logger.info(e.getMessage());
            throw new ObjectStorageException("TODO");
        }
    }

    public InputStream readAsStream(final String bucket, final String path) throws ObjectStorageException {
        // Handle exception if file does not exist.
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build()
            );

        } catch (InvalidKeyException | IOException | MinioException | NoSuchAlgorithmException e) {
            logger.info(e.getMessage());
            throw new ObjectStorageException("TODO");
        }
    }

    public void storeFile(final String bucket, final String path, final Long clientSideFileSize, final InputStream inputStream) throws ObjectStorageException {
        configureBucket(bucket);
        store(bucket, path, inputStream);
        if (stat(bucket, path).size() != clientSideFileSize) {
            throw new ObjectStorageException("TODO");
        }
    }

    public void delete(final String bucket, final String path) throws ObjectStorageException {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build()
            );
        } catch (InvalidKeyException | IOException | MinioException | NoSuchAlgorithmException e) {
            logger.info(e.getMessage());
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
    private void store(final String bucket, final String path, final InputStream inputStream) throws ObjectStorageException {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .stream(inputStream, inputStream.available(), -1)
                            .build()
            );
        } catch (InvalidKeyException | IOException | MinioException | NoSuchAlgorithmException e) {
            logger.info(e.getMessage());
            throw new ObjectStorageException("TODO");
        }
    }
}
