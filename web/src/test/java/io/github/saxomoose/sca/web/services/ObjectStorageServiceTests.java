package io.github.saxomoose.sca.web.services;

import io.github.saxomoose.sca.web.models.Tenant;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ObjectStorageServiceTests {
    @Autowired
    HttpSession httpSession;
    @Autowired
    private ObjectStorageService objectStorageService;

    @BeforeAll
    public void setup() {
        final var tenant = Tenant.of("demo_tenant_1");
        httpSession.setAttribute("tenant", tenant);
    }

    // given → when → then
    @Test
    public void getFileInfo() {
    }
}
