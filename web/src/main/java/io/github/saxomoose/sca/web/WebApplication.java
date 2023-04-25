package io.github.saxomoose.sca.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = { RedisReactiveAutoConfiguration.class, })
@ComponentScan(basePackages = { "io.github.saxomoose.sca.lib.services",
        "io.github.saxomoose.sca.web.config", "io.github.saxomoose.sca.web.controllers",
        "io.github.saxomoose.sca.web.services" })
// @ComponentScan(excludeFilters = @ComponentScan.Filter(
// type = FilterType.ASSIGNABLE_TYPE,
// classes = {DatabaseSeeder.class}))
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
