package io.github.saxomoose.sca.web.config.security;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.SecurityContextRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;
import io.github.saxomoose.sca.web.services.ValidationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PKIAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private static final Logger logger = LoggerFactory.getLogger(PKIAuthenticationProcessingFilter.class);

    private final ObjectMapper objectMapper;
    private final ValidationService validationService;

    /**
     * TODO: error handling? How to report back to user.
     */
    public PKIAuthenticationProcessingFilter(
        final AuthenticationManager authenticationManager,
        final SecurityContextRepository securityContextRepository,
        final PKIAuthenticationFailureHandler authenticationFailureHandler,
        final PKIAuthenticationSuccessHandler authenticationSuccessHandler, 
        final ObjectMapper objectMapper,
        final String defaultFilterProcessesUrl,
        final ValidationService validationService)
    {
        super(defaultFilterProcessesUrl);
        this.setAuthenticationManager(authenticationManager);
        this.setSecurityContextRepository(securityContextRepository);
        this.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        this.setAuthenticationFailureHandler(authenticationFailureHandler);
        this.objectMapper = objectMapper;
        this.validationService = validationService;
    }

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException, IOException, ServletException
    {
        if (HttpMethod.valueOf(request.getMethod()) != HttpMethod.POST) {
            logger.error("HTTP method not supported: %s", request.getMethod());
            throw new RuntimeException();
        }
        final String contentType = request.getHeader("Content-Type");
        if (contentType == null || !contentType.startsWith("application/json")) {
            logger.error("Content type not supported: %s", contentType);
            throw new RuntimeException();
        }
        final AuthenticationPayload authenticationPayload = objectMapper.readValue(request.getReader(), AuthenticationPayload.class);
        final PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(null, authenticationPayload);
        // TODO: validation should happen sooner
        try {
            validationService.authentication(authenticationPayload);
        } catch (BusinessLogicException exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException();
        }
        return getAuthenticationManager().authenticate(token);
    }
}
