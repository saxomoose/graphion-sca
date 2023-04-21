package io.github.saxomoose.sca.web.services;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.util.UUID;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("target")
public class ObjectStorageServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(ObjectStorageServiceTest.class);

    @Autowired
    private MinIOService minIOService;
    private String bucket;

    @BeforeAll
    public void setup() {
        bucket = "graphion";
    }

    @AfterAll
    public void deleteObject() throws Exception {
        minIOService.delete(bucket, path());
    }

    @Test
    public void storeObject() throws Exception {
        final var name = UUID.nameUUIDFromBytes("20230308-test-document".getBytes());
        final var path = String.format("/files/%s", name);
        final var inputStream = new FileInputStream(new ClassPathResource("minio/20230308-test-document.pdf").getFile());
        final var clientSideFileSize = (long) inputStream.available();
        minIOService.storeFile(bucket, path, clientSideFileSize, inputStream);
    }

    // given → when → then
    @Test
    public void statObject() throws Exception {
        final var statObjectResponse = minIOService.stat(bucket, path());
    }

    @Test
    public void getObjectAsStream() throws Exception {
        final var objectInputStream = minIOService.readAsStream(bucket, path());
    }

    private String path() {
        final var name = UUID.nameUUIDFromBytes("20230308-seed-document".getBytes());
        return String.format("/files/%s", name);
    }
}
