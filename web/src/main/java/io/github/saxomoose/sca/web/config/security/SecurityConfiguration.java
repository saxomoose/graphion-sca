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

import io.github.saxomoose.sca.web.config.security.jdbc.JDBCUserDetailsService;
import io.github.saxomoose.sca.web.config.security.pki.AuthenticationProcessingFilter;
import io.github.saxomoose.sca.web.config.security.pki.ExcludeSessionRepositoryFilter;
import io.github.saxomoose.sca.web.config.security.pki.PKIAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final ExcludeSessionRepositoryFilter excludeSessionRepositoryFilter;
    private final JDBCUserDetailsService jdbcUserDetailsService;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final PKIAuthenticationProvider pkiAuthenticationProvider;

    @Autowired
    public SecurityConfiguration(AuthenticationEntryPoint authenticationEntryPoint,
            AuthenticationFailureHandler authenticationFailureHandler,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            ExcludeSessionRepositoryFilter excludeSessionRepositoryFilter,
            JDBCUserDetailsService jdbcUserDetailsService, ObjectMapper objectMapper,
            PasswordEncoder passwordEncoder, PKIAuthenticationProvider pkiAuthenticationProvider) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.excludeSessionRepositoryFilter = excludeSessionRepositoryFilter;
        this.jdbcUserDetailsService = jdbcUserDetailsService;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
        this.pkiAuthenticationProvider = pkiAuthenticationProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    public void registerProvider(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(pkiAuthenticationProvider)
            .userDetailsService(jdbcUserDetailsService).passwordEncoder(passwordEncoder);
                

    }

    // @Bean
    // public FilterRegistrationBean<ExcludeSessionRepositoryFilter> loggingFilter(){
    // FilterRegistrationBean<ExcludeSessionRepositoryFilter> registrationBean = new FilterRegistrationBean<>();
        
    // registrationBean.setFilter(excludeSessionRepositoryFilter);
    // registrationBean.addUrlPatterns("/pki/**");
    // registrationBean.setOrder(1);
        
    // return registrationBean;    
    // }

    /**
     * Spring Security uses session created by redis integration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(AuthenticationManager authenticationManager, HttpSecurity http) throws Exception {
        http
            .requestCache((cache) -> cache.requestCache(new NullRequestCache()))
            .sessionManagement((session) -> session.sessionFixation().changeSessionId()
            .sessionCreationPolicy(SessionCreationPolicy.NEVER))
            // .addFilter(excludeSessionRepositoryFilter)
            .addFilterBefore(new AuthenticationProcessingFilter(
                authenticationManager,
                objectMapper,
                "/pki/login"), UsernamePasswordAuthenticationFilter.class)
            // .exceptionHandling()
            // .authenticationEntryPoint(authenticationEntryPoint).and()
            .authorizeHttpRequests().requestMatchers("/", "/css/**", "/js/**", "/login", "/pki/**", "/svg/**", "/svg-bootstrap/**").permitAll()
            .anyRequest().authenticated()    
                .and()
            // .authorizeHttpRequests().requestMatchers("/", "/register",
            // "/ping").permitAll()
            // Deactivate basic auth.
            // .authorizeHttpRequests().requestMatchers("/**").permitAll()
            // .requestMatchers("/css/**", "/js/**", "/svg/**",
            // "/svg-bootstrap/**").permitAll()
            // .anyRequest().authenticated()
            // .and()
            .formLogin().loginPage("/login").permitAll()
            // Redirect to closings
            .successHandler(authenticationSuccessHandler)
            .failureHandler(authenticationFailureHandler)
                .and()
            .logout().logoutUrl("/logout")
            .permitAll().logoutSuccessUrl("/");
        return http.build();
    }
}
