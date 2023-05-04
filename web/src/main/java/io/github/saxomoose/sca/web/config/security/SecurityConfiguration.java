package io.github.saxomoose.sca.web.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final DaoAuthenticationProvider jdbcAuthenticationProvider;
    private final JDBCAuthenticationFailureHandler jdbcAuthenticationFailureHandler;
    private final JDBCAuthenticationSuccessHandler jdbcAuthenticationSuccessHandler;
    private final ObjectMapper objectMapper;
    private final PKIAuthenticationFailureHandler pkiAuthenticationFailureHandler;
    private final PKIAuthenticationProvider pkiAuthenticationProvider;
    private final PKIAuthenticationSuccessHandler pkiAuthenticationSuccessHandler;

    @Autowired
    public SecurityConfiguration (
        AuthenticationEntryPoint authenticationEntryPoint,
        AuthenticationManagerBuilder authenticationManagerBuilder,
        JDBCAuthenticationFailureHandler authenticationFailureHandler,
        JDBCAuthenticationSuccessHandler authenticationSuccessHandler,
        JDBCUserDetailsService jdbcUserDetailsService,
        ObjectMapper objectMapper,
        PasswordEncoder passwordEncoder,
        PKIAuthenticationFailureHandler pkiAuthenticationFailureHandler,
        PKIAuthenticationProvider pkiAuthenticationProvider,
        PKIAuthenticationSuccessHandler pkiAuthenticationSuccessHandler) throws Exception
    {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jdbcAuthenticationFailureHandler = authenticationFailureHandler;
        this.jdbcAuthenticationSuccessHandler = authenticationSuccessHandler;
        this.objectMapper = objectMapper;
        this.pkiAuthenticationFailureHandler = pkiAuthenticationFailureHandler;
        this.pkiAuthenticationProvider = pkiAuthenticationProvider;
        this.pkiAuthenticationSuccessHandler = pkiAuthenticationSuccessHandler;
        this.jdbcAuthenticationProvider = new DaoAuthenticationProvider(passwordEncoder);
        this.jdbcAuthenticationProvider.setUserDetailsService(jdbcUserDetailsService);
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain pkiSecurityFilterChain( 
        AuthenticationManager authenticationManager,
        HttpSecurity httpSecurity) throws Exception
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
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity) throws Exception 
    {
        httpSecurity
            .authenticationProvider(jdbcAuthenticationProvider)
            .requestCache((cache) -> cache.requestCache(new NullRequestCache()))
           .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
               .and()
            .sessionManagement((session) -> session
                .sessionFixation().changeSessionId()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER))
            .authorizeHttpRequests((requests) -> requests
               .requestMatchers("/", "/css/**", "/js/**", "/login", "/logout", "/ping", "/svg/**", "/svg-bootstrap/**").permitAll()
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
