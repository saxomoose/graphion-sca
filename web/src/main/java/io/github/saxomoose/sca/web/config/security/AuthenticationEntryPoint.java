package io.github.saxomoose.sca.web.config.security;

import io.github.saxomoose.sca.web.dtos.Notification;
import io.github.saxomoose.sca.web.enums.NotificationType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import java.io.IOException;
import java.util.List;

@Component
public class AuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        final var flashMap = new FlashMap();
        final FlashMapManager flashMapManager = new SessionFlashMapManager();
        if (authException instanceof InsufficientAuthenticationException) {
            final var notifications = List.of(new Notification(NotificationType.ERROR, "notification.error.401", "http.401"));
            flashMap.put("notifications", notifications);
            flashMapManager.saveOutputFlashMap(flashMap, request, response);
        }

        response.sendRedirect("/login");
    }
}
