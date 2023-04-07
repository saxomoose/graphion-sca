package io.github.saxomoose.sca.worker.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class RedisConfiguration {
    private final RedisConnectionFactory redisConnectionFactory;

    @Autowired
    public RedisConfiguration(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }
}
