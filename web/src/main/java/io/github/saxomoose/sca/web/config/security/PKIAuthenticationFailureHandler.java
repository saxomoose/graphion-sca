package io.github.saxomoose.sca.web.config.security;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class PKIAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static final Logger logger = LoggerFactory.getLogger(PKIAuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException 
    {
        logger.error(exception.getMessage());
        final HttpSession session = request.getSession(false);
        if (session != null) {
            logger.info("Invalidating session: %S", session.getId());
            session.invalidate();
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
    }
}

