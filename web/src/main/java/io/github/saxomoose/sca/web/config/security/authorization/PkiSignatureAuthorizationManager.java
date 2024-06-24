package io.github.saxomoose.sca.web.config.security.authorization;

import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import io.github.saxomoose.sca.web.models.User;
import io.github.saxomoose.sca.web.services.ClosingService;

@Component
public class PkiSignatureAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private final ClosingService closingService;

    @Autowired
    public PkiSignatureAuthorizationManager(ClosingService closingService) {
        this.closingService = closingService;
    }

    @Override
    public AuthorizationDecision check(
        Supplier<Authentication> authentication, 
        RequestAuthorizationContext context) 
    {
        Long userId = ((User) authentication.get().getPrincipal()).id();
        Long rawClosingId = Long.parseLong(context.getVariables().get("rawClosingId"));
        UUID rawFileUUID = UUID.fromString(context.getVariables().get("rawFileUUID"));
        
        return new AuthorizationDecision(true);
    }    
}
