package io.github.saxomoose.sca.web.config.security;

import java.io.IOException;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.saxomoose.sca.web.utils.Notification;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    public AccessDeniedHandler(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException, ServletException
    {
        final var flashMap = new FlashMap();
        final var flashMapManager = new SessionFlashMapManager();
        final var notifications = List.of(new Notification(
            Notification.Type.ERROR,
            "notification.error.403",
            "http.403"));
        flashMap.put("notifications", notifications);
        flashMapManager.saveOutputFlashMap(flashMap, request, response);
        if (request.getHeader("X-Requested-With").equals("XMLHttpRequest")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            response.sendRedirect("/");
        }
    }
    
}
