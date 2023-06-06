package io.github.saxomoose.sca.web.services;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.saxomoose.sca.web.enums.Redis;
import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;
import io.github.saxomoose.sca.web.utils.Job;
import io.github.saxomoose.sca.web.utils.Notification;
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
    public void sendJobToQueue(Long tenantId)
    {
        final var job = new Job(tenantId, Job.Type.UPLOAD, "message", Map.of("", ""));
        String serializedJob;
        try {
            serializedJob = objectMapper.writeValueAsString(job);
            Objects.requireNonNull(serializedJob);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
        final var record = StreamRecords
            .rawBytes(Map.of("payload".getBytes(), serializedJob.getBytes()))
            .withStreamKey(Redis.JOB_KEY.toString().getBytes());
        RecordId recordId;
        try {
            recordId = redisConnection.streamCommands().xAdd(
                record,
                RedisStreamCommands.XAddOptions.maxlen(5).approximateTrimming(Boolean.TRUE));
            Objects.requireNonNull(recordId);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
        final var outputStringKey = String.format("%s:%s:%s", Redis.JOB_KEY, tenantId, recordId);
        try {
            redisConnection.stringCommands().set(
                outputStringKey.getBytes(),
                serializedJob.getBytes(),
                Expiration.from(Duration.ofHours(1)),
                SetOption.SET_IF_ABSENT);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
        // redisConnection.listCommands().lPush(RedisKey.JOBS.toString().getBytes(),
        // serializedJob.getBytes());
    }

    public void lockFile(UUID fileUUID) throws BusinessLogicException
    {
        try {
            final var result = stringRedisTemplate.opsForValue().setIfAbsent(
                String.format("%s:%s", Redis.WORKBENCH_KEY, fileUUID),
                String.valueOf(1),
                Duration.ofMinutes(1));
            if (!result) {
                throw new BusinessLogicException(
                    new Notification(
                        Notification.Type.INFORMATION,
                        "signature",
                        "signature.file.locked"));
            }
        } catch (BusinessLogicException exception) {
            logger.error(exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    public void unlockFile(UUID fileUUID)
    {
        try {
            stringRedisTemplate.delete(
                String.format("%s:%s", Redis.WORKBENCH_KEY, 
                fileUUID));
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    /**
     * TODO: email validation
     */
    public Integer getUserPINCode(Long id)
    {
        return 0;
    }

    /**
     * TODO
     */
    public void setUserPINCode(Long id)
    {
        final var pinCode = ThreadLocalRandom.current().nextInt(100000, 1000000);
        stringRedisTemplate.opsForValue().set(
            String.format("%s:%s", Redis.USER_PINCODE_KEY, id),
            String.valueOf(pinCode),
            Duration.ofDays(2));
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
