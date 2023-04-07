package io.github.saxomoose.sca.web.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.saxomoose.sca.web.enums.RedisKey;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.utils.Job;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RedisService {
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisScript<String> script;
    private final ObjectMapper objectMapper;
    private final HttpSession httpSession;
    private final RedisConnection redisConnection;


    // Replace with stringredistemplate
    @Autowired
    public RedisService(StringRedisTemplate redisTemplate, RedisScript<String> script, ObjectMapper objectMapper, HttpSession httpSession, RedisConnection redisConnection) {
        this.stringRedisTemplate = redisTemplate;
        this.script = script;
        this.objectMapper = objectMapper;
        this.httpSession = httpSession;
        this.redisConnection = redisConnection;
    }

    public void submitUploadJob() throws JsonProcessingException {
        final var tenantId = ((Tenant) httpSession.getAttribute("tenant")).id();
        final var redisStreamKey = String.format("%s:%s", RedisKey.JOBS, tenantId);
        final var job = new Job(tenantId);
        final var serializedJob = objectMapper.writeValueAsString(job);
        final var record = StreamRecords
                .rawBytes(Map.of("payload".getBytes(), serializedJob.getBytes()))
                .withStreamKey(redisStreamKey.getBytes());
        redisConnection.multi();
        redisConnection.streamCommands().xAdd(
                record,
                RedisStreamCommands.XAddOptions.maxlen(50).approximateTrimming(Boolean.TRUE)
        );
        redisConnection.listCommands().lPush(RedisKey.JOBS.toString().getBytes(), serializedJob.getBytes());
        redisConnection.exec();
    }

//    public void consumeUploadJobs() {
//        stringRedisTemplate.execute(new RedisCallback<String>() {
//            public Object doInRedis(RedisConnection connection) throws DataAccessException {
//                Long size = connection.dbSize();
//                // Can cast to StringRedisConnection if using a StringRedisTemplate
//                ((StringRedisConnection)connection).bRPop(1, UPLOAD_JOBS_CHANNEL);
//            }
//        });
//    }
}
