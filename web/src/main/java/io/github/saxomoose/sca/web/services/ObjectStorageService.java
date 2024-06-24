package io.github.saxomoose.sca.web.services;

import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;

// Singleton by default.
@Service
public class ObjectStorageService {
    private static final Logger logger = LoggerFactory.getLogger(ObjectStorageService.class);

    private final MinioClient minioClient;

    @Autowired
    public ObjectStorageService(MinioClient minioClient)
    {
        this.minioClient = minioClient;
    }

    public StatObjectResponse statObject(String bucket, String path)
    {
        try {
            return minioClient.statObject(StatObjectArgs.builder().bucket(bucket).object(path).build());
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    public Iterable<Result<Item>> listObjects(String bucket, String path)
    {
        return minioClient.listObjects(ListObjectsArgs.builder().bucket(bucket).prefix(path).recursive(true).build());
    }

    public byte[] objectAsBytes(String bucket, String path) {
        try {
            return objectAsStream(bucket, path).readAllBytes();
        } catch(Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    public InputStream objectAsStream(String bucket, String path)
    {
        // Handle exception if file does not exist.
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(path).build());
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    /**
     * WriteResponseObject contains versionId.
     */
    public void storeObject(String bucket, String path, InputStream inputStream)
    {
        try {
            minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(path)
                    .stream(inputStream, inputStream.available(), -1).build());
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    public void deleteObject(String bucket, String path)
    {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(path).build());
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }
}
