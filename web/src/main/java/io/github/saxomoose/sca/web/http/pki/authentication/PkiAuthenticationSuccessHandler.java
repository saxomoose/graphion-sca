package io.github.saxomoose.sca.web.http.pki.authentication;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.saxomoose.sca.web.config.security.PkiAuthentication;
import io.github.saxomoose.sca.web.enums.PathEnum;
import io.github.saxomoose.sca.web.enums.SessionEnum;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.models.User;
import io.github.saxomoose.sca.web.models.transfer.pki.ResponsePayload;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class PkiAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(PkiAuthenticationSuccessHandler.class);

    private final HttpSession httpSession;
    private final ObjectMapper objectMapper;

    // Should also determine destination as this information should be kept in Authentication object.
    public PkiAuthenticationSuccessHandler(HttpSession httpSession, ObjectMapper objectMapper)
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
        final Tenant defaultTenant = ((PkiAuthentication) authentication).defaultTenant();
        String redirectUrl;
        if(defaultTenant != null) {
            httpSession.setAttribute(SessionEnum.TENANT.toString(), defaultTenant);
            redirectUrl = String.format("/%s", PathEnum.CLOSINGS);
        } else {
            redirectUrl = String.format(
                "/%s/%s", 
                PathEnum.TENANTS, 
                ((User) authentication.getPrincipal()).id());
        }   

        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Content-Type", "application/json");
        final String serializedAuthSuccess = objectMapper.writeValueAsString(new ResponsePayload(redirectUrl));
        response.getWriter().write(serializedAuthSuccess);
    }
}
