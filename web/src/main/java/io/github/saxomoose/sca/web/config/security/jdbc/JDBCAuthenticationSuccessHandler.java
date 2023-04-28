package io.github.saxomoose.sca.web.config.security.jdbc;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import io.github.saxomoose.sca.web.services.TenantService;
import io.github.saxomoose.sca.web.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class JDBCAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final HttpSession httpSession;
    private final TenantService tenantService;
    private final UserService userService;

    @Autowired
    public JDBCAuthenticationSuccessHandler(
        HttpSession httpSession,
        TenantService tenantService,
        UserService userService) {
        this.httpSession = httpSession;
        this.tenantService = tenantService;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
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
