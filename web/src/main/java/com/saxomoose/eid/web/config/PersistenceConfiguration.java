package com.saxomoose.eid.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class PersistenceConfiguration {
    @Bean
    AuditorAware<Long> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
