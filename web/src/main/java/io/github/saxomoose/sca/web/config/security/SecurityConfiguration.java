package io.github.saxomoose.sca.web.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.NullRequestCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.saxomoose.sca.web.services.ValidationService;

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
    public SecurityContextRepository securityContextRepository()
    {
       return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
        final AccessDeniedHandler accessDeniedHandler,
        final AuthenticationManager authenticationManager,
        final HttpSecurity httpSecurity,
        final SecurityContextRepository securityContextRepository,
        final ObjectMapper objectMapper,
        final PKIAuthenticationFailureHandler pkiAuthenticationFailureHandler,
        final PKIAuthenticationProvider pkiAuthenticationProvider,
        final PKIAuthenticationSuccessHandler pkiAuthenticationSuccessHandler,
        final ValidationService validationService) throws Exception
    {
        httpSecurity
            .requestCache((cache) -> cache.requestCache(new NullRequestCache()))
            .exceptionHandling((exceptionHandling) -> exceptionHandling
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .sessionFixation().changeSessionId())
            .authenticationProvider(pkiAuthenticationProvider)
            .addFilterBefore(new PKIAuthenticationProcessingFilter(
                authenticationManager,
                securityContextRepository,
                pkiAuthenticationFailureHandler,
                pkiAuthenticationSuccessHandler,
                objectMapper,
                "/pki/login",
                validationService
                ), UsernamePasswordAuthenticationFilter.class)
            .formLogin((formLogin) -> formLogin.disable())
            .httpBasic((httpBasic) -> httpBasic.disable())
            .logout((logout) -> logout
                .logoutUrl("/logout")
                .deleteCookies("SESSION")
                .logoutSuccessUrl("/"))
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers(
                    "/css/**", "/js/**", "/svg/**", "/svg-bootstrap/**",
                    "/", "/logout", "/ping",
                    "/pki/challenge").permitAll()
                .requestMatchers("/closings", "/documents").access(new WebExpressionAuthorizationManager("hasAnyRole('ADMIN', 'USER', 'SIGNATORY')"))
                // .anyRequest().hasRole("ADMIN"));
                .anyRequest().permitAll());
        return httpSecurity.build();
    }
}
