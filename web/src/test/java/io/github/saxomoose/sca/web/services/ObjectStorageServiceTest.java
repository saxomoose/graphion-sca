package io.github.saxomoose.sca.web.services;

import java.io.FileInputStream;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
// @Tag("target")
public class ObjectStorageServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(ObjectStorageServiceTest.class);

    @Autowired
    private MinIOService minioService;
    private String bucket;

    @BeforeAll
    public void setup() {
        bucket = "graphion";
    }

    @AfterAll
    public void deleteObject() throws Exception {
        minioService.deleteObject(bucket, path());
    }

    @Test
    public void storeObject() throws Exception 
    {
        final var name = UUID.nameUUIDFromBytes("20230308-test-document".getBytes());
        final var path = String.format("/files/%s", name);
        final var inputStream = new FileInputStream(new ClassPathResource("minio/20230308-test-document.pdf").getFile());
        final var clientSideFileSize = (long) inputStream.available();
        minioService.storeObject(bucket, path, inputStream);
    }

    // given → when → then
    @Test
    public void statObject() throws Exception 
    {
        final var statObjectResponse = minioService.statObject(bucket, path());
    }

    @Test
    public void getObjectAsStream() throws Exception 
    {
        final var objectInputStream = minioService.objectAsStream(bucket, path());
    }

    private String path() 
    {
        final var name = UUID.nameUUIDFromBytes("20230308-seed-document".getBytes());
        return String.format("/files/%s", name);
    }
}
