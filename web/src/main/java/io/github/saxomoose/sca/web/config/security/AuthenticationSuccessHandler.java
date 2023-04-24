package io.github.saxomoose.sca.web.config.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.github.saxomoose.sca.web.repositories.db.TenantRepository;
import io.github.saxomoose.sca.web.repositories.db.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthenticationSuccessHandler
        implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {
    private final HttpSession httpSession;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    @Autowired
    public AuthenticationSuccessHandler(HttpSession httpSession, UserRepository userRepository,
            TenantRepository tenantRepository) {
        this.httpSession = httpSession;
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        final var userId = userRepository.findByUsername(authentication.getName()).id();
        final var tenant = tenantRepository.findById(userId).orElseThrow();
        httpSession.setAttribute("tenant", tenant);
        // final var uri =
        // UriComponentsBuilder.fromPath("/documents").queryParam("tenant",
        // tenant.id()).toUriString();
        response.sendRedirect("/documents");
    }
}
