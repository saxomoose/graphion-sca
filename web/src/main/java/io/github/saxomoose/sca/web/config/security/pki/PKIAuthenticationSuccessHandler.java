package io.github.saxomoose.sca.web.config.security.pki;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class PKIAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(PKIAuthenticationSuccessHandler.class);

    private ObjectMapper objectMapper;

    public PKIAuthenticationSuccessHandler(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException
    {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Content-Type", "application/json");
        final var serializedAuthSuccess = objectMapper.writeValueAsString(new AuthSuccess(authentication.getName(),
                authentication.getAuthorities().stream().map(GrantedAuthority::toString).toList()));
        ;
        response.getWriter().write(serializedAuthSuccess);
    }
}
