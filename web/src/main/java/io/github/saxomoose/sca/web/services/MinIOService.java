package io.github.saxomoose.sca.web.services;

import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;

// Singleton by default.
@Service
public class MinIOService {
    private static final Logger logger = LoggerFactory.getLogger(MinIOService.class);

    private final MinioClient minioClient;

    @Autowired
    public MinIOService(MinioClient minioClient)
    {
        this.minioClient = minioClient;
    }

    // private byte[] getFileAsBytes(String bucket, String name) {
    // byte[] outBytes = null;
    // InputStream inputStream = null;
    // try {
    // final var statObjectResponse = statObject(name);
    // final var size = statObjectResponse.size();
    // inputStream = getFileAsStream()
    // }
    // }

    public StatObjectResponse stat(final String bucket, final String path)
    {
        try {
            return minioClient.statObject(StatObjectArgs.builder().bucket(bucket).object(path).build());
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    public byte[] objectAsBytes(final String bucket, final String path) {
        try {
            return objectAsStream(bucket, path).readAllBytes();
        } catch(Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    public InputStream objectAsStream(final String bucket, final String path)
    {
        // Handle exception if file does not exist.
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(path).build());
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    public void storeFile(final String bucket, final String path, final InputStream inputStream) {
        store(bucket, path, inputStream);
    }

    public void storeFile(final String bucket, final String path, final Long clientSideFileSize,
            final InputStream inputStream)
    {
        configureBucket(bucket);
        store(bucket, path, inputStream);
        if (stat(bucket, path).size() != clientSideFileSize) {
            logger.error("The file size stored in the bucket does not match the client-side file size.");
            throw new RuntimeException();
        }
    }

    public void delete(final String bucket, final String path)
    {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(path).build());
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    /**
     * Should only create 1 bucket per tenant.
     */
    private void configureBucket(String bucket)
    {
        try {
            final var bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    /**
     * WriteResponseObject contains versionId.
     */
    private void store(final String bucket, final String path, final InputStream inputStream)
    {
        try {
            minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(path)
                    .stream(inputStream, inputStream.available(), -1).build());
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }
}
