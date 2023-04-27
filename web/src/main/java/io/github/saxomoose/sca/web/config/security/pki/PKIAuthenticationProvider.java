package io.github.saxomoose.sca.web.config.security.pki;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import eu.webeid.security.challenge.ChallengeNonceStore;
import eu.webeid.security.validator.AuthTokenValidator;

/**
 * Custom authentication provider that uses the authentication token validator for authentication.
 */
@Component
public class PKIAuthenticationProvider implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(PKIAuthenticationProvider.class);
    private static final String ROLE = "ROLE_SIGNATORY";
    private static final GrantedAuthority GRANTED_AUTHORITY = new SimpleGrantedAuthority(ROLE);

    private final AuthTokenValidator authTokenValidator;
    private final ChallengeNonceStore challengeNonceStore;

    @Autowired
    public PKIAuthenticationProvider(AuthTokenValidator authTokenValidator,
            ChallengeNonceStore challengeNonceStore) {
        this.authTokenValidator = authTokenValidator;
        this.challengeNonceStore = challengeNonceStore;

    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        logger.info(String.format("authenticate(): %s", auth));
        final var authentication = (PreAuthenticatedAuthenticationToken) auth;
        final var authToken = ((AuthToken) authentication.getCredentials()).token();
        final var authorities = List.of(GRANTED_AUTHORITY);
        try {
            final var nonce = challengeNonceStore.getAndRemove().getBase64EncodedNonce();
            final var userCertificate = authTokenValidator.validate(authToken, nonce);
            return PKIAuthentication.of(userCertificate, authorities);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.equals(authentication);
    }

}
