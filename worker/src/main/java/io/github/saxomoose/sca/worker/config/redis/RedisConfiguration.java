package io.github.saxomoose.sca.worker.config.redis;

import io.github.saxomoose.sca.worker.services.ExceptionHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class RedisConfiguration {
    private final RedisConnectionFactory redisConnectionFactory;
    private final ExceptionHandlerService exceptionHandlerService;

    @Autowired
    public RedisConfiguration(RedisConnectionFactory redisConnectionFactory, ExceptionHandlerService exceptionHandlerService) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.exceptionHandlerService = exceptionHandlerService;
    }

    @Bean
    public RedisConnection redisConnection() {
        try {
            return redisConnectionFactory.getConnection();
        } catch (Exception exception) {
        }
        return null;
    }
    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }
}
