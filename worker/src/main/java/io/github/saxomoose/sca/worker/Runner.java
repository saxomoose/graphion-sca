package io.github.saxomoose.sca.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.saxomoose.sca.worker.services.ExceptionHandlerService;
import io.lettuce.core.RedisBusyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class Runner implements ApplicationListener<ApplicationStartedEvent>, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(Runner.class);

    private final Environment environment;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisConnection redisConnection;
    private final ObjectMapper objectMapper;
    private final ExceptionHandlerService exceptionHandlerService;
    private volatile Boolean flag = Boolean.TRUE;

    @Autowired
    public Runner(Environment environment, StringRedisTemplate stringRedisTemplate, RedisConnection redisConnection, ObjectMapper objectMapper, ExceptionHandlerService exceptionHandlerService) {
        this.environment = environment;
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisConnection = redisConnection;
        this.objectMapper = objectMapper;
        this.exceptionHandlerService = exceptionHandlerService;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent ignored) {
        // Replace 2nd argument by injected env variable.
//        final var secondaryQueueKey = String.format("%s:%s", RedisKey.JOBS, environment.getProperty("spring.application.name"));
        createQueueWithConsumerGroup();
            do {
                try {
                    final var mapRecord = readMessageFromQueue();
                    final var processedJob = processJob(mapRecord);
                    if (processedJob.status().equals(Job.Status.SUCCESSFUL)) {
                        acknowledgeMessage(mapRecord.getId());
                    }
                    sendResultToOutputString(mapRecord, processedJob);
                } catch (Throwable throwable) {
                    exceptionHandlerService.handle(throwable);
                }
            } while (flag);
    }

    @Override
    public void destroy() {
        flag = Boolean.FALSE;
    }

    private void createQueueWithConsumerGroup() {
        // XGROUP CREATE graphionscaweb:jobs standardgroup 0-0 MKSTREAM
        try {
            redisConnection.streamCommands().xGroupCreate(
                    Redis.Queue.KEY.toString().getBytes(),
                    Redis.Queue.STANDARD_CONSUMER_GROUP.toString(),
                    ReadOffset.from(RecordId.of("0-0")),
                    Boolean.TRUE

            );
        } catch (RedisSystemException e) {
            // If consumer group already exists, continue. It should be possible to restart worker without deleting the queue.
            if (e.getCause() instanceof RedisBusyException) {
             logger.info(e.getCause().getMessage());
            }
        }
    }

    private MapRecord<String, String, String> readMessageFromQueue() {
        // XREADGROUP GROUP standardgroup standardconsumer COUNT 1 STREAMS graphionscaweb:jobs >
        @SuppressWarnings("unchecked")
        final var records = redisConnection.streamCommands().xReadGroup(
                    Consumer.from(Redis.Queue.STANDARD_CONSUMER_GROUP.toString(), Redis.Queue.STANDARD_CONSUMER.toString()),
                    StreamReadOptions.empty().count(1).block(Duration.ZERO),
                    StreamOffset.create(Redis.Queue.KEY.toString().getBytes(), ReadOffset.lastConsumed())
        );
        return records.stream().findFirst().get().deserialize(new StringRedisSerializer());
    }

    private Job processJob(MapRecord<String, String, String> mapRecord) throws JsonProcessingException {
        final var job = objectMapper.readValue(mapRecord.getValue().get("payload"), Job.class);
        job.setStatus(Job.Status.SUCCESSFUL);
        return job;
    }

    private void acknowledgeMessage(RecordId recordId) {
        redisConnection.streamCommands().xAck(
                Redis.Queue.KEY.toString().getBytes(),
                Redis.Queue.STANDARD_CONSUMER_GROUP.toString(),
                recordId
        );
    }

    private void sendResultToOutputString(MapRecord<String, String, String> mapRecord, Job processedJob) throws JsonProcessingException {
        final var outputStringKey = String.format("%s:%s:%s", Redis.Queue.KEY, processedJob.tenantId(), mapRecord.getId());
        final var serializedJob = objectMapper.writeValueAsString(processedJob);
        stringRedisTemplate.opsForValue().setIfPresent(outputStringKey,serializedJob , Duration.ofDays(1));
    }
}
