package io.github.saxomoose.sca.web.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.savedrequest.NullRequestCache;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.saxomoose.sca.web.config.security.jdbc.JDBCAuthenticationFailureHandler;
import io.github.saxomoose.sca.web.config.security.jdbc.JDBCAuthenticationSuccessHandler;
import io.github.saxomoose.sca.web.config.security.jdbc.JDBCUserDetailsService;
import io.github.saxomoose.sca.web.config.security.pki.PKIAuthenticationFailureHandler;
import io.github.saxomoose.sca.web.config.security.pki.PKIAuthenticationProcessingFilter;
import io.github.saxomoose.sca.web.config.security.pki.PKIAuthenticationProvider;
import io.github.saxomoose.sca.web.config.security.pki.PKIAuthenticationSuccessHandler;

@Configuration 
@EnableWebSecurity
public class SecurityConfiguration {
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    public SecurityConfiguration (AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain pkiSecurityFilterChain( 
        final AuthenticationManager authenticationManager,
        final ObjectMapper objectMapper,
        final PKIAuthenticationFailureHandler pkiAuthenticationFailureHandler,
        final PKIAuthenticationProvider pkiAuthenticationProvider,
        final PKIAuthenticationSuccessHandler pkiAuthenticationSuccessHandler,
        final HttpSecurity httpSecurity) throws Exception
    {
        httpSecurity
            .securityMatcher("/pki/**")
            .authenticationProvider(pkiAuthenticationProvider)
            .requestCache((cache) -> cache.requestCache(new NullRequestCache()))
            .sessionManagement((session) -> session
                .sessionFixation().changeSessionId()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER))
            .addFilterBefore(new PKIAuthenticationProcessingFilter(
                authenticationManager,
                pkiAuthenticationFailureHandler,
                pkiAuthenticationSuccessHandler,
                objectMapper,
                new HttpSessionSecurityContextRepository(),
                "/pki/login"
                ), UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/pki/challenge").permitAll()
                .anyRequest().authenticated());
        return httpSecurity.build();
    }

    /**
     * Spring Security uses session created by redis integration.
     */
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(
        final HttpSecurity httpSecurity,
        final JDBCAuthenticationFailureHandler jdbcAuthenticationFailureHandler,
        final JDBCAuthenticationSuccessHandler jdbcAuthenticationSuccessHandler,
        final JDBCUserDetailsService jdbcUserDetailsService,
        final PasswordEncoder passwordEncoder) throws Exception 
    {
        final var jdbcAuthenticationProvider = new DaoAuthenticationProvider(passwordEncoder);
        jdbcAuthenticationProvider.setUserDetailsService(jdbcUserDetailsService);
        httpSecurity
            .authenticationProvider(jdbcAuthenticationProvider)
            .requestCache((cache) -> cache.requestCache(new NullRequestCache()))
            .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
               .and()
            .sessionManagement((session) -> session
                .sessionFixation().changeSessionId()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER))
            .authorizeHttpRequests((requests) -> requests
               .requestMatchers(
                    "/css/**", "/js/**", "/svg/**", "/svg-bootstrap/**",
                    "/", "/login", "/logout", "/ping").permitAll()
               .anyRequest().authenticated())
            // Deactivate basic auth.
            // .authorizeHttpRequests().requestMatchers("/**").permitAll()
            .formLogin().loginPage("/login")
            // Redirect to closings
                .successHandler(jdbcAuthenticationSuccessHandler)
                .failureHandler(jdbcAuthenticationFailureHandler)
                .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/");
        return httpSecurity.build();
    }
}
