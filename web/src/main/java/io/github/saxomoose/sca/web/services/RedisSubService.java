package io.github.saxomoose.sca.web.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.saxomoose.sca.web.utils.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.SubscriptionListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class RedisSubService implements MessageListener, SubscriptionListener {
    private static final Logger logger = LoggerFactory.getLogger(RedisSubService.class);

    private final ObjectMapper objectMapper;

    public RedisSubService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onChannelSubscribed(byte[] channel, long count) {
        logger.info(String.format("RedisSubService subscribed to channel %s", new String(channel, StandardCharsets.UTF_8)));
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
