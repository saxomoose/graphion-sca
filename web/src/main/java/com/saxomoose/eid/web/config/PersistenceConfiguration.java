package com.saxomoose.eid.web.config;

import com.saxomoose.eid.web.models.Tenant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableTransactionManagement
public class PersistenceConfiguration {
    @Bean
    AuditorAware<Tenant> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
