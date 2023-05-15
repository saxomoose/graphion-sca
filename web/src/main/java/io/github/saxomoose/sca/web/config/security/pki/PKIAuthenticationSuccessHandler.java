package io.github.saxomoose.sca.web.config.security.pki;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.saxomoose.sca.web.services.TenantService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class PKIAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(PKIAuthenticationSuccessHandler.class);

    private HttpSession httpSession;
    private ObjectMapper objectMapper;
    private TenantService tenantService;

    public PKIAuthenticationSuccessHandler(HttpSession httpSession, ObjectMapper objectMapper, TenantService tenantService)
    {
        this.httpSession = httpSession;
        this.objectMapper = objectMapper;
        this.tenantService = tenantService;
    }

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException
    {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        // final var tenantId = ((User) authentication.getPrincipal()).tenantId();
        // final var tenant = tenantService.select(tenantId);
        // httpSession.setAttribute("tenant", tenant);
    }
}
