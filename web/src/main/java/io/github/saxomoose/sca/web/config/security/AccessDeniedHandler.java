package io.github.saxomoose.sca.web.config.security;

import java.io.IOException;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
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

    /**
     * If request is AJAX, the handler sets response status on 403.
     */
    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException, ServletException
    {
        FlashMap flashMap = new FlashMap();
        FlashMapManager flashMapManager = new SessionFlashMapManager();
        flashMap.put(
            "notifications", 
             List.of(new Notification(Notification.Type.ERROR, "notification.error.403", "http.403")));
        flashMapManager.saveOutputFlashMap(flashMap, request, response);
        if (request.getHeader("X-Requested-With").equals("XMLHttpRequest")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            response.sendRedirect("/");
        }
    }
}
