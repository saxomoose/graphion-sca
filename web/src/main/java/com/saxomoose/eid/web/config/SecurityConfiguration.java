package com.saxomoose.eid.web.config;

import com.saxomoose.eid.web.services.JdbcUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    public void registerProvider(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jdbcUserDetailsService)

            .passwordEncoder(passwordEncoder)
                .and();
//            .authenticationProvider();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
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
                .permitAll())
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll());

        return http.build();
    }
}
