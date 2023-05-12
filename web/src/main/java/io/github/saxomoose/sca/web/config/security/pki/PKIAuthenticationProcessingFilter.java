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
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
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
        final HttpSessionSecurityContextRepository httpSessionSecurityContextRepository,
        final PKIAuthenticationFailureHandler authenticationFailureHandler,
        final PKIAuthenticationSuccessHandler authenticationSuccessHandler, 
        final ObjectMapper objectMapper,
        final String defaultFilterProcessesUrl,
        final ValidationService validationService)
    {
        super(defaultFilterProcessesUrl);
        this.setAuthenticationManager(authenticationManager);
        this.setSecurityContextRepository(httpSessionSecurityContextRepository);
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
        final var contentType = request.getHeader("Content-Type");
        if (contentType == null || !contentType.startsWith("application/json")) {
            logger.error("Content type not supported: %s", contentType);
            throw new RuntimeException();
        }
        final var auth = objectMapper.readValue(request.getReader(), io.github.saxomoose.sca.web.config.security.pki.Authentication.class);
        final var token = new PreAuthenticatedAuthenticationToken(null, auth);
        try {
            validationService.authentication(auth);
        } catch (BusinessLogicException exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException();
        }
        return getAuthenticationManager().authenticate(token);
    }

    // TODO: override successfulAuthentication

    // @Override
    // public void successfulAuthentication(
    // HttpServletRequest request,
    // HttpServletResponse response,
    // FilterChain chain,
    // Authentication authResult) throws IOException, ServletException
    // {
    // final var context = super.securityContextHolderStrategy.createEmptyContext();
    // context.setAuthentication(authResult);
    // this.securityContextHolderStrategy.setContext(context);
    // this.securityContextRepository.saveContext(context, request, response);
    // if (this.logger.isDebugEnabled()) {
    // this.logger.debug(LogMessage.format("Set SecurityContextHolder to %s", authResult));
    // }
    // this.successHandler.onAuthenticationSuccess(request, response, authResult);
    // }

}
