package io.github.saxomoose.sca.worker.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.github.saxomoose.sca.worker.services.ExceptionHandlerService;

@Configuration
@EnableTransactionManagement
public class RedisConfiguration {
    private final ExceptionHandlerService exceptionHandlerService;

    @Autowired
    public RedisConfiguration(ExceptionHandlerService exceptionHandlerService) {
        this.exceptionHandlerService = exceptionHandlerService;
    }

    @Bean
    public RedisConnection redisConnection(final RedisConnectionFactory redisConnectionFactory) {
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
