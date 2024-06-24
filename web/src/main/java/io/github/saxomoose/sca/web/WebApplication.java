package io.github.saxomoose.sca.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
    basePackages = {
        "io.github.saxomoose.sca.lib.services",
        "io.github.saxomoose.sca.web.config",
        "io.github.saxomoose.sca.web.http", 
        "io.github.saxomoose.sca.web.services" })
public class WebApplication {
    public static void main(String[] args)
    {
        SpringApplication.run(WebApplication.class, args);
    }
}
