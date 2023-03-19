package io.github.saxomoose.sca.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(
        exclude = {
                JacksonAutoConfiguration.class
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
