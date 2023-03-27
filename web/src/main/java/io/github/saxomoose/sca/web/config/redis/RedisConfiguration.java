package io.github.saxomoose.sca.web.config.redis;

import io.github.saxomoose.sca.web.enums.PubSubChannel;
import io.github.saxomoose.sca.web.services.RedisSubService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.List;


@Configuration
public class RedisConfiguration {
    private final RedisConnectionFactory redisConnectionFactory;
    private final RedisSubService redisSubService;

    public RedisConfiguration(RedisConnectionFactory redisConnectionFactory, RedisSubService redisSubService) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.redisSubService = redisSubService;
    }

    @Bean
    public RedisMessageListenerContainer container() {
        final var container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(redisSubService, List.of(
                ChannelTopic.of(PubSubChannel.JOBS.channel()))
        );
        return container;
    }

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
