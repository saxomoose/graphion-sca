package io.github.saxomoose.sca.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.netty.NettyAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(
    exclude = { 
        NettyAutoConfiguration.class,
        RedisReactiveAutoConfiguration.class,
        SqlInitializationAutoConfiguration.class,
        TaskExecutionAutoConfiguration.class,
        TaskSchedulingAutoConfiguration.class,
        WebSocketServletAutoConfiguration.class }) 
@ComponentScan(
    basePackages = {
        "io.github.saxomoose.sca.lib.services",
        "io.github.saxomoose.sca.web.config",
        "io.github.saxomoose.sca.web.http", 
        "io.github.saxomoose.sca.web.services" })
// @ComponentScan(excludeFilters = @ComponentScan.Filter(
// type = FilterType.ASSIGNABLE_TYPE,
// classes = {DatabaseSeeder.class}))
public class WebApplication {
    public static void main(String[] args)
    {
        SpringApplication.run(WebApplication.class, args);
    }
}
