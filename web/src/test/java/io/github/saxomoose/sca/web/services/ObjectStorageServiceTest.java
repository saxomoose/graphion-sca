package io.github.saxomoose.sca.web.services;

import io.github.saxomoose.sca.web.exceptions.MinIOServiceException;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ObjectStorageServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(ObjectStorageServiceTest.class);

    @Autowired
    private MinIOService minIOService;
    private String bucket;
    private String path;
    private Long clientSideFileSize;
    private InputStream inputStream;

    @BeforeAll
    public void setup() {
        bucket = "sca-test";
        final var name = UUID.nameUUIDFromBytes("20230308-test-document".getBytes());
        path = String.format("/files/%s", name);
        try {
            inputStream = new FileInputStream(new ClassPathResource("objectstorage/20230308-test-document.pdf").getFile());
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        try {
            clientSideFileSize = (long) inputStream.available();
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }

    @Test
    @Order(1)
    public void storeObject() {
        try {
            minIOService.storeFile(bucket, path, clientSideFileSize, inputStream);
        } catch (MinIOServiceException e) {
            logger.info(e.getMessage());
        }
    }

    // given → when → then
    @Test
    @Order(2)
    public void statObject() throws MinIOServiceException {
        final var statObjectResponse = minIOService.stat(bucket, path);
    }

    @Test
    @Order(3)
    public void getObjectAsStream() {
        try {
            final var objectInputStream = minIOService.readAsStream(bucket, path);
        } catch (MinIOServiceException e) {
            logger.info(e.getMessage());
        }
    }

    @Test
    @Order(4)
    public void deleteObject() {
        try {
            minIOService.delete(bucket, path);
        } catch (MinIOServiceException e) {
            logger.info(e.getMessage());
        }
    }
}
