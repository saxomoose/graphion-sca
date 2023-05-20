package io.github.saxomoose.sca.web.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

@Configuration
public class RedisConfiguration {
    // @Bean
    // public RedisConnectionFactory redisConnectionFactory() {
    // RedisStandaloneConfiguration config = new
    // RedisStandaloneConfiguration("server", 6379);
    // return new LettuceConnectionFactory();
    // }
    @Bean
    public RedisConnection redisConnection(RedisConnectionFactory redisConnectionFactory) 
    {
        return redisConnectionFactory.getConnection();
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) 
    {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    // @Bean
    // public StatefulRedisConnection<String, String>
    // statefulRedisConnection(RedisConnection
    // redisConnection) {
    // return ((RedisAsyncCommandsImpl)
    // redisConnection.getNativeConnection()).getStatefulConnection();
    // }
    // @Bean
    // public RedisCommands<String, String>
    // syncCommands(StatefulRedisConnection<String, String>
    // statefulRedisConnection) {
    // return statefulRedisConnection.sync();
    // }
    @Bean
    public RedisScript<String> script() 
    {
        final var scriptSource = new ResourceScriptSource(
            new ClassPathResource("redis/job-id.lua")).getResource();
        return RedisScript.of(scriptSource, String.class);
    }

    // @Bean
    // public RedisMessageListenerContainer container() {
    // final var container = new RedisMessageListenerContainer();
    // container.setConnectionFactory(redisConnectionFactory);
    // container.addMessageListener(redisSubService, List.of(
    // ChannelTopic.of(RedisStreamKey.JOBS.toString()))
    // );
    // return container;
    // }

    // @Bean
    // public MessageListenerAdapter uploadJobListenerAdapter() {
    // return new MessageListenerAdapter(receiver(), "handleUploadJob");
    // }
    //
    // @Bean
    // public MessageListenerAdapter signingJobListenerAdapter() {
    // return new MessageListenerAdapter(receiver(), "handleSigningJob");
    // }
    //
    // @Bean
    // Receiver receiver() {
    // return new Receiver();
    // }
}
