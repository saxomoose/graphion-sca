package io.github.saxomoose.sca.web.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfiguration {
    private final Environment environment;

    public RedisConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration =
            new RedisStandaloneConfiguration(
                    environment.getRequiredProperty("spring.data.redis.host"),
                    Integer.parseInt(environment.getRequiredProperty("spring.data.redis.port")));
        redisStandaloneConfiguration.setUsername(environment.getRequiredProperty("spring.data.redis.username"));
        redisStandaloneConfiguration.setPassword(environment.getRequiredProperty("spring.data.redis.password"));
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        return lettuceConnectionFactory;
    }

    @Bean
    public RedisConnection redisConnection() 
    {
        return redisConnectionFactory().getConnection();
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(final RedisConnectionFactory redisConnectionFactory) 
    {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
