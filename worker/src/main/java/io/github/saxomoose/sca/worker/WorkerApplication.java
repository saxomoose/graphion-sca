package io.github.saxomoose.sca.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

//@SpringBootApplication(
//        scanBasePackages = "io.github.saxomoose.sca.lib.services"
//)
//public class WorkerApplication {
//    private static final Logger logger = LoggerFactory.getLogger(WorkerApplication.class);
//
//    public static void main(String[] args) {
//            SpringApplication.run(WorkerApplication.class, args);
//        }
//}
