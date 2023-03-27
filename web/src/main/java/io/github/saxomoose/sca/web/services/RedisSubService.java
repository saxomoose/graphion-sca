package io.github.saxomoose.sca.web.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.saxomoose.sca.web.utils.Job;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class RedisSubService implements MessageListener {
    private final ObjectMapper objectMapper;

    public RedisSubService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        final var channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        Job job;
        try {
            job = objectMapper.readValue(message.toString(), Job.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(message);
    }
}
