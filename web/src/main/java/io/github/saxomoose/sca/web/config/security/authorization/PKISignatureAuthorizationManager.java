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
public class PKISignatureAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private final ClosingService closingService;

    @Autowired
    public PKISignatureAuthorizationManager(ClosingService closingService) {
        this.closingService = closingService;
    }

    @Override
    public AuthorizationDecision check(
        Supplier<Authentication> authentication, 
        RequestAuthorizationContext context) 
    {
        final Long userId = ((User) authentication.get().getPrincipal()).id();
        final Long rawClosingId = Long.parseLong(context.getVariables().get("rawClosingId"));
        final UUID rawFileUUID = UUID.fromString(context.getVariables().get("rawFileUUID"));
        if (closingService.userIsSignatory(rawClosingId, userId , rawFileUUID)) {
            return new AuthorizationDecision(true);
        } else {
            return new AuthorizationDecision(false);
        }
    }    
}
