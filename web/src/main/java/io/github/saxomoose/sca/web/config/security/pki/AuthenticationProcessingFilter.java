package io.github.saxomoose.sca.web.config.security.pki;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationProcessingFilter.class);

    private final ObjectMapper objectMapper;

    /**
     * TODO: Look into alternative.
     */
    public AuthenticationProcessingFilter(
        AuthenticationManager authenticationManager,
        PKIAuthenticationFailureHandler authenticationFailureHandler,
        PKIAuthenticationSuccessHandler authenticationSuccessHandler,
        ObjectMapper objectMapper, 
        String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
        this.setAuthenticationManager(authenticationManager);
        this.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        this.setAuthenticationFailureHandler(authenticationFailureHandler);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        if (!HttpMethod.POST.name().equals(request.getMethod())) {
            logger.error("HTTP method not supported: %s", request.getMethod());
            throw new RuntimeException();
        }
        final var contentType = request.getHeader("Content-Type");
        if (contentType == null || !contentType.startsWith("application/json")) {
            logger.error("Content type not supported: %s", contentType);
            throw new RuntimeException();
        }
        AuthToken authToken = null;
        try {
            authToken = objectMapper.readValue(request.getReader(), AuthToken.class);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException();
        }
        final var token = new PreAuthenticatedAuthenticationToken(null, authToken);
        return getAuthenticationManager().authenticate(token);
    }
}
