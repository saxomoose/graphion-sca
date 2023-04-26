package io.github.saxomoose.sca.web.config.security.pki;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class PKIAuthenticationProvider implements AuthenticationProvider {

    private final HttpServletRequest request;
    private final ObjectMapper objectMapper;

    @Autowired
    public PKIAuthenticationProvider(HttpServletRequest request, ObjectMapper objectMapper) {
        this.request = request;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        if (!HttpMethod.POST.name().equals(request.getMethod())) {
            throw new AuthenticationServiceException(
                    String.format("HTTP method not supported: %s", request.getMethod()));
        }
        final var contentType = request.getHeader("Content-Type");
        if (contentType == null || !contentType.startsWith("application/json")) {
            throw new AuthenticationServiceException(
                    String.format("Content type not supported: %s", contentType));
        }
        AuthToken authToken = null;
        try {
            authToken = objectMapper.readValue(request.getReader(), AuthToken.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Failed to deserialize JSON payload.");
        }
        final var token = new PreAuthenticatedAuthenticationToken(null, authToken);
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.equals(authentication);
    }

}
