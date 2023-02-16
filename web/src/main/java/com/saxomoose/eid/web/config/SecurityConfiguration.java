package com.saxomoose.eid.web.config;

import com.saxomoose.eid.web.services.JdbcUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final DataSource dataSource;
    private final PasswordEncoder passwordEncoder;
    private final JdbcUserDetailsService jdbcUserDetailsService;

    @Autowired
    public SecurityConfiguration(DataSource dataSource, PasswordEncoder passwordEncoder, JdbcUserDetailsService jdbcUserDetailsService) {
        this.dataSource = dataSource;
        this.passwordEncoder = passwordEncoder;
        this.jdbcUserDetailsService = jdbcUserDetailsService;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
//            .authenticationProvider(authTokenDTOAuthenticationProvider)
            .jdbcAuthentication()
            .dataSource(dataSource)
            .passwordEncoder(passwordEncoder)
            .authoritiesByUsernameQuery("select username,role from users where username = ?");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic()
            .and()
            .authorizeHttpRequests((requests) -> requests
//                    .requestMatchers("/", "/register", "/logout").permitAll()
                    // Comment out to enable security.
                    .requestMatchers("/**").permitAll()
                    .requestMatchers("/css/**", "/js/**", "/svg/**", "/svg-bootstrap/**").permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin((form) -> form
                    .loginPage("/login")
                    .loginProcessingUrl("/perform_login")
                    .defaultSuccessUrl("/closings", true)
                    .permitAll()
            )
            .logout()
                .logoutUrl("/perform_logout")
                .logoutSuccessUrl("/")
                .permitAll();

        return http.build();
    }
}
