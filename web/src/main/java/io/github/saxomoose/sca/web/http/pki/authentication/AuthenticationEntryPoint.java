package io.github.saxomoose.sca.web.http.pki.authentication;

import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.InsufficientAuthenticationException;
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
public class AuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {
    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException 
    {
        FlashMap flashMap = new FlashMap();
        FlashMapManager flashMapManager = new SessionFlashMapManager();
        if (authException instanceof InsufficientAuthenticationException) {
            flashMap.put(
                "notifications",
                List.of(new Notification(Notification.Type.ERROR, "notification.error.401", "http.401")));
            flashMapManager.saveOutputFlashMap(flashMap, request, response);
        }
        response.sendRedirect("/");
    }
}
