package io.github.saxomoose.sca.web.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;
import io.github.saxomoose.sca.web.models.Tenant;
import jakarta.servlet.http.HttpSession;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("target")
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

    // @Test
    // public void submitUploadJob() 
    // {
    //     try {
    //         redisService.sendJobToQueue();
    //     } catch (Exception exception) {
    //         throw new RuntimeException(exception);
    //     }
    // }

    @Test
    public void lockSameFileTwiceReturnsFalse() throws BusinessLogicException
    {
        redisService.lockFile(UUID.fromString("8d4c8242-a99a-4843-8331-c97a2a41b722"));
        assertThrows(
            BusinessLogicException.class,
            () -> redisService.lockFile(UUID.fromString("8d4c8242-a99a-4843-8331-c97a2a41b722")));
    }


}
