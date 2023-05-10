package io.github.saxomoose.sca.web.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import io.github.saxomoose.sca.web.models.Tenant;
import jakarta.servlet.http.HttpSession;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RedisServiceTest {
    @Autowired
    private RedisService redisService;
    @Autowired
    private HttpSession httpSession;

    @BeforeAll
    public void setup() 
    {
        httpSession.setAttribute("tenant", new Tenant(1L, null, null));
    }

    @Test
    public void submitUploadJob() 
    {
        try {
            redisService.sendJobToQueue();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
