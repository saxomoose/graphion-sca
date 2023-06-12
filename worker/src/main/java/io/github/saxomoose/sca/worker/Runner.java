package io.github.saxomoose.sca.worker;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties.Job;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.saxomoose.sca.worker.services.ExceptionHandlerService;
import io.lettuce.core.RedisBusyException;

@Component
public class Runner implements DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(Runner.class);

    private final Environment environment;
    private final RedisConnection redisConnection;
    private final StringRedisTemplate stringRedisTemplate;
    private final StringRedisSerializer stringRedisSerializer;
    private final ObjectMapper objectMapper;
    private final ExceptionHandlerService exceptionHandlerService;
    private volatile Boolean flag = Boolean.TRUE;

    @Autowired
    public Runner(Environment environment, RedisConnection redisConnection, StringRedisTemplate stringRedisTemplate, StringRedisSerializer stringRedisSerializer, ObjectMapper objectMapper, ExceptionHandlerService exceptionHandlerService) {
        this.environment = environment;
        this.redisConnection = redisConnection;
        this.stringRedisTemplate = stringRedisTemplate;
        this.stringRedisSerializer = stringRedisSerializer;
        this.objectMapper = objectMapper;
        this.exceptionHandlerService = exceptionHandlerService;
    }

    /**
     * If consumer group already exists, continue. It should be possible to restart worker without deleting the queue.
     */
    @EventListener
    public void onApplicationStartedEvent(ApplicationStartedEvent ignored) {
        // Replace 2nd argument by injected env variable.
//        final var secondaryQueueKey = String.format("%s:%s", RedisKey.JOBS, environment.getProperty("spring.application.name"));
        try {
            createQueueWithConsumerGroup();
        } catch (Throwable throwable) {
            if (throwable.getCause() instanceof RedisBusyException) {
                logger.info(throwable.getCause().getMessage());
            } else {
                exceptionHandlerService.handle(throwable);
            }
        }
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

    /**
     * XGROUP CREATE graphionscaweb:jobs standardgroup 0-0 MKSTREAM
     */
    private void createQueueWithConsumerGroup() {
        final var createStream = !stringRedisTemplate.hasKey(Redis.Queue.KEY.toString());
        redisConnection.streamCommands().xGroupCreate(
                Redis.Queue.KEY.toString().getBytes(),
                Redis.Queue.STANDARD_CONSUMER_GROUP.toString(),
                ReadOffset.from(RecordId.of("0-0")),
                createStream
        );
    }

    /**
     * XREADGROUP GROUP standardgroup standardconsumer COUNT 1 STREAMS graphionscaweb:jobs >
     * TODO: replace hard coded consumer name by injected env variable.
     */
    private MapRecord<String, String, String> readMessageFromQueue() {
        final var consumerName = String.format("%s-%s", environment.getProperty("spring.application.name"), 1);
        final var records = redisConnection.streamCommands().xReadGroup(
                    Consumer.from(Redis.Queue.STANDARD_CONSUMER_GROUP.toString(), consumerName),
                    StreamReadOptions.empty().count(1).block(Duration.ZERO),
                    StreamOffset.create(Redis.Queue.KEY.toString().getBytes(), ReadOffset.lastConsumed())
        );
        return records.stream().findFirst().get().deserialize(stringRedisSerializer);
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
