package io.github.saxomoose.sca.web.config.db;

import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import io.github.saxomoose.sca.web.models.Closing;
import io.github.saxomoose.sca.web.models.Document;
import io.github.saxomoose.sca.web.models.LegalPerson;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.models.User;

@Configuration
@EnableTransactionManagement
public class DBConfiguration {
    @Bean
    AuditorAware<Long> auditorProvider() {
        return new AuditorAware<Long>() {
            @Override
            public Optional<Long> getCurrentAuditor() {
                return Optional
                    .ofNullable(SecurityContextHolder.getContext())
                    .map(SecurityContext::getAuthentication)
                    .filter(Authentication::isAuthenticated)
                    .map(Authentication::getPrincipal)
                    .map(User.class::cast)
                    .map(User::id);
            }
        };
    }

    @Bean
    DataSourceInitializer initializer(DataSource dataSource) {
        var initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        return initializer;
    }

    @Bean
    NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    TransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    RowMapperCollection rowMappers() 
    {
        return new RowMapperCollection()
            .registerRowMapper(User.class, new UserRowMapper())
            .registerRowMapper(Tenant.class, new TenantRowMapper())
            .registerRowMapper(LegalPerson.class, new LegalPersonRowMapper())
            .registerRowMapper(Closing.class, new ClosingRowMapper())
            .registerRowMapper(Document.class, new DocumentRowMapper());
    }
}
