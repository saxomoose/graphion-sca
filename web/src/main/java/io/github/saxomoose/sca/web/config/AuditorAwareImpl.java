package io.github.saxomoose.sca.web.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import io.github.saxomoose.sca.web.models.User;

public class AuditorAwareImpl implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication).filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal).map(User.class::cast).map(User::id);
    }
}
