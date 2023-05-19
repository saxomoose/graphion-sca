package io.github.saxomoose.sca.web.config.security;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class PKIAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(PKIAuthenticationSuccessHandler.class);
    private static final String CLOSINGS_URL  = "/closings";
    private static final String TENANTS_URL = "/tenants";

    private final HttpSession httpSession;
    private final ObjectMapper objectMapper;

    // Should also determine destination as this information should be kept in Authentication object.
    public PKIAuthenticationSuccessHandler(HttpSession httpSession, ObjectMapper objectMapper)
    {
        this.httpSession = httpSession;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException
    {
        final Tenant defaultTenant = ((PKIAuthentication) authentication).defaultTenant();
        String successURL;
        if(defaultTenant != null) {
            httpSession.setAttribute("tenant", defaultTenant);
            successURL = CLOSINGS_URL;
        } else {
            successURL = String.format("%s/%s", TENANTS_URL, ((User) authentication.getPrincipal()).uuid());
        }   

        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Content-Type", "application/json");
        final var serializedAuthSuccess = objectMapper.writeValueAsString(
            new AuthenticationSuccessPayload(successURL));
        response.getWriter().write(serializedAuthSuccess);
    }
}
