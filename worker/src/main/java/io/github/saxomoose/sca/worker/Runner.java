package io.github.saxomoose.sca.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.saxomoose.sca.worker.services.ExceptionHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisListCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class Runner implements ApplicationListener<ApplicationStartedEvent>, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(Runner.class);

    private final Environment environment;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final ExceptionHandlerService exceptionHandlerService;
    private volatile Boolean flag = Boolean.TRUE;

    @Autowired
    public Runner(Environment environment, StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper, ExceptionHandlerService exceptionHandlerService) {
        this.environment = environment;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
        this.exceptionHandlerService = exceptionHandlerService;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent ignored) {
        do {
            try {
                readFromPrimaryQueue();
                readFromSecondaryQueue();
                processJob();
            } catch (Throwable throwable) {
                exceptionHandlerService.handle(throwable);
            }
        } while (flag);
    }

    @Override
    public void destroy() throws Exception {
        flag = Boolean.FALSE;
    }

    private void readFromPrimaryQueue() throws JsonProcessingException {
        final var secondaryListKey = new StringBuilder()
                .append(RedisListKey.JOBS)
                .append(":")
                // Replace by injected env variable.
                .append(environment.getProperty("spring.application.name")).toString();
        var targetElement = moveJobToSecondaryQueue(secondaryListKey);
        do {
            targetElement = processJob(secondaryListKey, targetElement);
        } while (targetElement != null);
    }

    private String moveJobToSecondaryQueue(String secondaryListKey) {
        return stringRedisTemplate.opsForList().move(
                RedisListKey.JOBS.toString(),
                RedisListCommands.Direction.RIGHT,
                secondaryListKey,
                RedisListCommands.Direction.LEFT,
                0,
                TimeUnit.SECONDS);
    }

    public String processJob(String secondaryListKey, String targetElement) throws JsonProcessingException {
        final var job= objectMapper.readValue(targetElement, Job.class);
        stringRedisTemplate.opsForList().remove(secondaryListKey, 1, targetElement);
        return stringRedisTemplate.opsForList().index(secondaryListKey, -1);
    }


}
