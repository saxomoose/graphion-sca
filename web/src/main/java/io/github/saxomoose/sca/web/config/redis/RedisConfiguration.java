package io.github.saxomoose.sca.web.config.redis;

import io.github.saxomoose.sca.web.services.RedisSubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;


@Configuration
public class RedisConfiguration {
    private final RedisConnectionFactory redisConnectionFactory;
    private final RedisSubService redisSubService;

    @Autowired
    public RedisConfiguration(RedisConnectionFactory redisConnectionFactory, RedisSubService redisSubService) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.redisSubService = redisSubService;
    }

    @Bean
    public RedisConnection redisConnection() {
        return redisConnectionFactory.getConnection();
    }

    @Bean
    public RedisScript<String> script() {

        final var scriptSource = new ResourceScriptSource(new ClassPathResource("redis/job-id.lua")).getResource();
        return RedisScript.of(scriptSource, String.class);
    }

//    @Bean
//    public RedisMessageListenerContainer container() {
//        final var container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(redisConnectionFactory);
//        container.addMessageListener(redisSubService, List.of(
//                ChannelTopic.of(RedisStreamKey.JOBS.toString()))
//        );
//        return container;
//    }

//    @Bean
//    public MessageListenerAdapter uploadJobListenerAdapter() {
//        return new MessageListenerAdapter(receiver(), "handleUploadJob");
//    }
//
//    @Bean
//    public MessageListenerAdapter signingJobListenerAdapter() {
//        return new MessageListenerAdapter(receiver(), "handleSigningJob");
//    }
//
//    @Bean
//    Receiver receiver() {
//        return new Receiver();
//    }
}
