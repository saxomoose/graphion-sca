package io.github.saxomoose.sca.web.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.saxomoose.sca.web.config.security.jdbc.JDBCAuthenticationFailureHandler;
import io.github.saxomoose.sca.web.config.security.jdbc.JDBCAuthenticationSuccessHandler;
import io.github.saxomoose.sca.web.config.security.jdbc.JDBCUserDetailsService;
import io.github.saxomoose.sca.web.config.security.pki.AuthenticationProcessingFilter;
import io.github.saxomoose.sca.web.config.security.pki.PKIAuthenticationFailureHandler;
import io.github.saxomoose.sca.web.config.security.pki.PKIAuthenticationProvider;
import io.github.saxomoose.sca.web.config.security.pki.PKIAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final JDBCAuthenticationFailureHandler jdbcAuthenticationFailureHandler;
    private final JDBCAuthenticationSuccessHandler jdbcAuthenticationSuccessHandler;
    private final JDBCUserDetailsService jdbcUserDetailsService;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final PKIAuthenticationFailureHandler pkiAuthenticationFailureHandler;
    private final PKIAuthenticationProvider pkiAuthenticationProvider;
    private final PKIAuthenticationSuccessHandler pkiAuthenticationSuccessHandler;

    @Autowired
    public SecurityConfiguration(
        AuthenticationEntryPoint authenticationEntryPoint,
        JDBCAuthenticationFailureHandler authenticationFailureHandler,
        JDBCAuthenticationSuccessHandler authenticationSuccessHandler,
        JDBCUserDetailsService jdbcUserDetailsService,
        ObjectMapper objectMapper,
        PasswordEncoder passwordEncoder,
        PKIAuthenticationFailureHandler pkiAuthenticationFailureHandler,
        PKIAuthenticationProvider pkiAuthenticationProvider,
        PKIAuthenticationSuccessHandler pkiAuthenticationSuccessHandler) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jdbcAuthenticationFailureHandler = authenticationFailureHandler;
        this.jdbcAuthenticationSuccessHandler = authenticationSuccessHandler;
        this.jdbcUserDetailsService = jdbcUserDetailsService;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
        this.pkiAuthenticationFailureHandler = pkiAuthenticationFailureHandler;
        this.pkiAuthenticationProvider = pkiAuthenticationProvider;
        this.pkiAuthenticationSuccessHandler = pkiAuthenticationSuccessHandler;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    public void registerProvider(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(pkiAuthenticationProvider)
            .userDetailsService(jdbcUserDetailsService).passwordEncoder(passwordEncoder);
    }

    /**
     * Spring Security uses session created by redis integration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(AuthenticationManager authenticationManager, HttpSecurity http) throws Exception {
        http.requestCache((cache) -> cache.requestCache(new NullRequestCache()))
            .sessionManagement((session) -> session
                .sessionFixation().changeSessionId()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER))
            .addFilterBefore(new AuthenticationProcessingFilter(
                authenticationManager,
                pkiAuthenticationFailureHandler,
                pkiAuthenticationSuccessHandler,
                objectMapper,
                "/pki/login"), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .and()
            .authorizeHttpRequests().requestMatchers("/", "/css/**", "/js/**", "/login", "/ping", "/pki/**", "/svg/**", "/svg-bootstrap/**").permitAll()
            .anyRequest().authenticated()    
                .and()
            // Deactivate basic auth.
            // .authorizeHttpRequests().requestMatchers("/**").permitAll()
            .formLogin().loginPage("/login").permitAll()
            // Redirect to closings
            .successHandler(jdbcAuthenticationSuccessHandler)
            .failureHandler(jdbcAuthenticationFailureHandler)
                .and()
            .logout().logoutUrl("/logout")
            .permitAll().logoutSuccessUrl("/");
        return http.build();
    }
}
