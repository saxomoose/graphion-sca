package io.github.saxomoose.sca.worker.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;

@Service
public class ExceptionHandlerService {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerService.class);

    private final ApplicationContext context;

    @Autowired
    public ExceptionHandlerService(ApplicationContext context) {
        this.context = context;
    }

    public void handle(Throwable throwable) {
        if (throwable instanceof RedisConnectionFailureException) {
            // Send a message to web app via pub/sub channel.
            logger.error(throwable.getMessage());
            final var exitCode = SpringApplication.exit(context, () -> 1);
            System.exit(exitCode);
        }
//        logger.info(throwable.getMessage());
    }
}
