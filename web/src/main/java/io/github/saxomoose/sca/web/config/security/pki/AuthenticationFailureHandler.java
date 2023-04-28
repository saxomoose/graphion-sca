package io.github.saxomoose.sca.web.config.security.pki;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static final Logger logger =
            LoggerFactory.getLogger(AuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        logger.error(exception.getMessage());
        final var session = request.getSession(false);
        if (session != null) {
            logger.info("Invalidating session: %S", session.getId());
            session.invalidate();
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
    }
}

