package com.saxomoose.eid.web.config.security;

import com.saxomoose.eid.web.services.JdbcUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final PasswordEncoder passwordEncoder;
    private final JdbcUserDetailsService jdbcUserDetailsService;

    @Autowired
    public SecurityConfiguration(PasswordEncoder passwordEncoder, JdbcUserDetailsService jdbcUserDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.jdbcUserDetailsService = jdbcUserDetailsService;
    }

    @Autowired
    public void registerProvider(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jdbcUserDetailsService)
            .passwordEncoder(passwordEncoder)
                .and()
        ;
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new com.saxomoose.eid.web.config.security.AuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new com.saxomoose.eid.web.config.security.AuthenticationEntryPoint();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationFailureHandler authenticationFailureHandler,
            AuthenticationEntryPoint authenticationEntryPoint) throws Exception {

        http.httpBasic()
            .and()
            .authorizeHttpRequests((requests) -> requests
                    .requestMatchers("/", "/register").permitAll()
                    // Comment out to enable security.
//                    .requestMatchers("/**").permitAll()
                    .requestMatchers("/css/**", "/js/**", "/svg/**", "/svg-bootstrap/**").permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/closings")
                .failureHandler(authenticationFailureHandler)
                .permitAll())
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll())
            .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);

        return http.build();
    }
}
