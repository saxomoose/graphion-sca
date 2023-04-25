package io.github.saxomoose.sca.web.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;

import io.github.saxomoose.sca.web.services.JDBCUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final PasswordEncoder passwordEncoder;
    private final JDBCUserDetailsService jdbcUserDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    public SecurityConfiguration(PasswordEncoder passwordEncoder,
            JDBCUserDetailsService jdbcUserDetailsService,
            AuthenticationEntryPoint authenticationEntryPoint,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler) {
        this.passwordEncoder = passwordEncoder;
        this.jdbcUserDetailsService = jdbcUserDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Autowired
    public void registerProvider(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jdbcUserDetailsService).passwordEncoder(passwordEncoder).and();
    }

    /**
     * Spring Security uses session created by redis integration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).and()
                .requestCache((cache) -> cache.requestCache(new NullRequestCache()))
                .sessionManagement(
                        (session) -> session
                                .sessionFixation().changeSessionId()
                                .sessionCreationPolicy(SessionCreationPolicy.NEVER))
                .authorizeHttpRequests().requestMatchers("/", "/register", "/ping").permitAll()
                // Deactivate basic auth.
                // .requestMatchers("/**").permitAll()
                .requestMatchers("/css/**", "/js/**", "/svg/**", "/svg-bootstrap/**").permitAll()
                .anyRequest().authenticated().and()
                .formLogin().loginPage("/login").permitAll()
                // Redirect to closings
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler).and()
                .logout().logoutUrl("/logout")
                .permitAll().logoutSuccessUrl("/");
        return http.build();
    }
}
