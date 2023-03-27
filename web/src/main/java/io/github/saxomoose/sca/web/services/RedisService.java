package io.github.saxomoose.sca.web.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.saxomoose.sca.web.enums.PubSubChannel;
import io.github.saxomoose.sca.web.utils.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RedisService {
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    // Replace with stringredistemplate
    @Autowired
    public RedisService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void submitUploadJob() {
        final var job = new Job(UUID.randomUUID());
        String serializedJob;
        try {
            serializedJob = objectMapper.writeValueAsString(job);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        stringRedisTemplate.convertAndSend(PubSubChannel.JOBS.channel(), serializedJob);
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
