package io.github.saxomoose.sca.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(
        exclude = {
                AopAutoConfiguration.class,
                JacksonAutoConfiguration.class,
                RedisReactiveAutoConfiguration.class,
                TaskExecutionAutoConfiguration.class,
                TaskSchedulingAutoConfiguration.class
        }
)
@ComponentScan(
        basePackages = {
                "io.github.saxomoose.sca.web.config",
                "io.github.saxomoose.sca.web.controllers",
                "io.github.saxomoose.sca.web.repositories",
                "io.github.saxomoose.sca.web.services"
        }
)
//@ComponentScan(excludeFilters = @ComponentScan.Filter(
//        type = FilterType.ASSIGNABLE_TYPE,
//        classes = {DatabaseSeeder.class}))
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
