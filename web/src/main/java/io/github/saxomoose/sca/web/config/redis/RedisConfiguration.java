package io.github.saxomoose.sca.web.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfiguration {
    @Bean
    public RedisConnection redisConnection(final RedisConnectionFactory redisConnectionFactory) 
    {
        return redisConnectionFactory.getConnection();
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(final RedisConnectionFactory redisConnectionFactory) 
    {
        final StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
