package io.github.saxomoose.sca.web.config.security.pki;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PKIAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger logger =
            LoggerFactory.getLogger(PKIAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Content-Type", "application/json");
        response.getWriter().write(AuthSuccess.asJson(authentication));
    }

    /**
     * TODO: Refactor. Alternative.
     */
    public static class AuthSuccess {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @JsonProperty("sub")
        private String sub;

        @JsonProperty("auth")
        private List<String> auth;

        public static String asJson(Authentication authentication) throws JsonProcessingException {
            final AuthSuccess dto = new AuthSuccess();
            dto.sub = authentication.getName();
            dto.auth = convertAuthorities(authentication.getAuthorities());
            return dto.objectMapper.writeValueAsString(dto);
        }

        private static List<String> convertAuthorities(
                Collection<? extends GrantedAuthority> authorities) {
            return authorities.stream().map(GrantedAuthority::toString)
                    .collect(Collectors.toList());
        }
    }
}
