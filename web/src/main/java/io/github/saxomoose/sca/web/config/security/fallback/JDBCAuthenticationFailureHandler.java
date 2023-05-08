package io.github.saxomoose.sca.web.config.security.fallback;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import io.github.saxomoose.sca.web.utils.Notification;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JDBCAuthenticationFailureHandler
        implements org.springframework.security.web.authentication.AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException
    {
        final var flashMap = new FlashMap();
        final FlashMapManager flashMapManager = new SessionFlashMapManager();
        if (exception instanceof BadCredentialsException) {
            final var notifications = List.of(new Notification(Notification.Type.ERROR,
                    "notification.error.authentication", "login.error.bad_credentials"));
            flashMap.put("notifications", notifications);
            flashMapManager.saveOutputFlashMap(flashMap, request, response);
        }
        response.sendRedirect("/login");
    }
}
