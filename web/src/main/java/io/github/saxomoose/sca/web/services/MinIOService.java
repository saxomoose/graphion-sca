package io.github.saxomoose.sca.web.services;

import io.github.saxomoose.sca.web.exceptions.MinIOServiceException;
import io.minio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

// Singleton by default.
@Service
public class MinIOService {
    private static final Logger logger = LoggerFactory.getLogger(MinIOService.class);

    private final MinioClient minioClient;

    @Autowired
    public MinIOService(
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

    public StatObjectResponse stat(final String bucket, final String path) throws MinIOServiceException {
        try {
            return minioClient.statObject(StatObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(path)
                    .build()
            );
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new MinIOServiceException();
        }
    }

    public InputStream readAsStream(final String bucket, final String path) throws MinIOServiceException {
        // Handle exception if file does not exist.
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build()
            );

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new MinIOServiceException();
        }
    }

    public void storeFile(final String bucket, final String path, final Long clientSideFileSize, final InputStream inputStream) throws MinIOServiceException {
        configureBucket(bucket);
        store(bucket, path, inputStream);
        if (stat(bucket, path).size() != clientSideFileSize) {
            logger.error("The file size stored in the bucket does not match the client-side file size.");
            throw new MinIOServiceException();
        }
    }

    public void delete(final String bucket, final String path) throws MinIOServiceException {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build()
            );
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new MinIOServiceException();
        }
    }

    // Should only create 1 bucket per tenant.
    private void configureBucket(String bucket) throws MinIOServiceException {
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
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new MinIOServiceException();
        }
    }

    // WriteResponseObject contains versionId.
    private void store(final String bucket, final String path, final InputStream inputStream) throws MinIOServiceException {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .stream(inputStream, inputStream.available(), -1)
                            .build()
            );
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new MinIOServiceException();
        }
    }
}
