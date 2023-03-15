package io.github.saxomoose.sca.web.config.security;

import io.github.saxomoose.sca.web.services.JDBCUserDetailsService;
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
    private final JDBCUserDetailsService jdbcUserDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    public SecurityConfiguration(PasswordEncoder passwordEncoder,
                                 JDBCUserDetailsService jdbcUserDetailsService,
                                 AuthenticationEntryPoint authenticationEntryPoint,
                                 AuthenticationFailureHandler authenticationFailureHandler) {
        this.passwordEncoder = passwordEncoder;
        this.jdbcUserDetailsService = jdbcUserDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }
    @Autowired
    public void registerProvider(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jdbcUserDetailsService)
            .passwordEncoder(passwordEncoder)
                .and()
        ;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests()
                .requestMatchers("/", "/register").permitAll()
                // Deactivate basic auth.
//                    .requestMatchers("/**").permitAll()
                .requestMatchers("/css/**", "/js/**", "/svg/**", "/svg-bootstrap/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login").permitAll()
                // Redirect to closings
                .loginPage("/documents")
                .failureHandler(authenticationFailureHandler)
                .and()
            .logout()
                .logoutUrl("/logout").permitAll()
                .logoutSuccessUrl("/")
                .and()
            .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
        return http.build();
    }
}
