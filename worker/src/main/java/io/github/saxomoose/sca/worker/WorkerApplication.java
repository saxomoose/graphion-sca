package io.github.saxomoose.sca.worker;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;

@SpringBootApplication(
        exclude = {
                RedisReactiveAutoConfiguration.class,
        }
//        scanBasePackages = "io.github.saxomoose.sca.lib.services"
)
public class WorkerApplication {
    private static final Logger logger = LoggerFactory.getLogger(WorkerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(WorkerApplication.class, args);
    }

    @PreDestroy
    public void onExit() {
        System.out.println();
    }


}
