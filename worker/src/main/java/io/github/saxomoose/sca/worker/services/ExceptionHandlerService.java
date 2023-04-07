package io.github.saxomoose.sca.worker.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExceptionHandlerService {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerService.class);

    public void handle(Throwable throwable) {
        logger.info(throwable.getMessage());
    }
}
