package io.github.saxomoose.sca.web.services;

import io.github.saxomoose.sca.web.exceptions.ObjectStorageException;
import io.github.saxomoose.sca.web.models.Tenant;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ObjectStorageServiceTest {
    @Autowired
    private ObjectStorageService objectStorageService;
    private Tenant tenant;

    @BeforeAll
    public void setup() {
        tenant = Tenant.of("demo_tenant_1");
    }

    // given → when → then
    @Test
    public void statObject() throws ObjectStorageException {

        final var statObjectResponse = objectStorageService.statObject("sca", "documents/5a105e8b-9d40-3132-9780-d62ea2265d8a");
        System.out.println();
    }
}
