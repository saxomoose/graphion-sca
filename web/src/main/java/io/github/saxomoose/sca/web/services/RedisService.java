package io.github.saxomoose.sca.web.services;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.saxomoose.sca.web.enums.Redis;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.utils.Job;
import jakarta.servlet.http.HttpSession;

@Service
public class RedisService {
    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);

    private final HttpSession httpSession;
    private final ObjectMapper objectMapper;
    private final RedisConnection redisConnection;
    private final RedisScript<String> script;
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RedisService(
        HttpSession httpSession,
        ObjectMapper objectMapper,
        RedisConnection redisConnection,
        RedisScript<String> script,
        StringRedisTemplate stringRedisTemplate) 
    {
        this.httpSession = httpSession;
        this.objectMapper = objectMapper;
        this.redisConnection = redisConnection;
        this.script = script;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * XADD graphionscaweb:jobs MAXLEN ~ 5 * "payload" "test1"
     */
    public void sendJobToQueue() 
    {
        final var tenantId = ((Tenant) httpSession.getAttribute("tenant")).id();
        final var job = new Job(tenantId, Job.Type.UPLOAD, "message", Map.of("", ""));
        String serializedJob;
        try {
            serializedJob = objectMapper.writeValueAsString(job);
            Objects.requireNonNull(serializedJob);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
        final var record =
                StreamRecords.rawBytes(Map.of("payload".getBytes(), serializedJob.getBytes()))
                        .withStreamKey(Redis.Queue.KEY.toString().getBytes());
        RecordId recordId;
        try {
            recordId = redisConnection.streamCommands().xAdd(record,
                    RedisStreamCommands.XAddOptions.maxlen(5).approximateTrimming(Boolean.TRUE));
            Objects.requireNonNull(recordId);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
        final var outputStringKey = String.format("%s:%s:%s", Redis.Queue.KEY, tenantId, recordId);
        try {
            stringRedisTemplate.opsForValue().setIfAbsent(outputStringKey, serializedJob,
                    Duration.ofDays(1));
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
        // redisConnection.listCommands().lPush(RedisKey.JOBS.toString().getBytes(),
        // serializedJob.getBytes());
    }

    // public void consumeUploadJobs() {
    // stringRedisTemplate.execute(new RedisCallback<String>() {
    // public Object doInRedis(RedisConnection connection) throws
    // DataAccessException {
    // Long size = connection.dbSize();
    // // Can cast to StringRedisConnection if using a StringRedisTemplate
    // ((StringRedisConnection)connection).bRPop(1, UPLOAD_JOBS_CHANNEL);
    // }
    // });
    // }
}
