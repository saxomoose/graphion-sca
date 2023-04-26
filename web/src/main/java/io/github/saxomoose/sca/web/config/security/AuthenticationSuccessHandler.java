package io.github.saxomoose.sca.web.config.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.github.saxomoose.sca.web.services.TenantService;
import io.github.saxomoose.sca.web.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthenticationSuccessHandler
        implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {
    private final HttpSession httpSession;
    private final UserService userService;
    private final TenantService tenantService;

    @Autowired
    public AuthenticationSuccessHandler(
            HttpSession httpSession,
            UserService userService,
            TenantService tenantService) {
        this.httpSession = httpSession;
        this.userService = userService;
        this.tenantService = tenantService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        final var userId = userService.select(authentication.getName()).id();
        final var tenant = tenantService.selectByUserId(userId);
        httpSession.setAttribute("tenant", tenant);
        // final var uri =
        // UriComponentsBuilder.fromPath("/documents").queryParam("tenant",
        // tenant.id()).toUriString();
        response.sendRedirect("/documents");
    }
}
