package io.github.saxomoose.sca.web.config.security.pki;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;

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

import eu.webeid.security.certificate.CertificateData;
import eu.webeid.security.challenge.ChallengeNonceStore;
import eu.webeid.security.validator.AuthTokenValidator;
import io.github.saxomoose.sca.web.models.Signatory;
import io.github.saxomoose.sca.web.services.SignatoryService;

/**
 * Custom authentication provider that uses the authentication token validator for authentication.
 */
@Component
public class PKIAuthenticationProvider implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(PKIAuthenticationProvider.class);
    private static final GrantedAuthority GRANTED_AUTHORITY = new SimpleGrantedAuthority("ROLE_SIGNATORY");

    private final AuthTokenValidator authTokenValidator;
    private final ChallengeNonceStore challengeNonceStore;
    private final SignatoryService signatoryService;

    @Autowired
    public PKIAuthenticationProvider(
        AuthTokenValidator authTokenValidator,
        ChallengeNonceStore challengeNonceStore,
        SignatoryService signatoryService) 
    {
        this.authTokenValidator = authTokenValidator;
        this.challengeNonceStore = challengeNonceStore;
        this.signatoryService = signatoryService;
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException 
    {
        logger.info(String.format("authenticate(): %s", auth));
        final var authentication = (PreAuthenticatedAuthenticationToken) auth;
        final var authToken = ((AuthToken) authentication.getCredentials()).token();
        final var authorities = List.of(GRANTED_AUTHORITY);
        try {
            final var nonce = challengeNonceStore.getAndRemove().getBase64EncodedNonce();
            final var userCertificate = authTokenValidator.validate(authToken, nonce);
            final var signatory = signatoryFromCertificate(userCertificate);
            return PKIAuthentication.of(signatory, authorities);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) 
    {
        return PreAuthenticatedAuthenticationToken.class.equals(authentication);
    }

    private Signatory signatoryFromCertificate(X509Certificate userCertificate) throws CertificateEncodingException 
    {   
        final var nationalRegistrationNumber = Objects.requireNonNull(CertificateData.getSubjectIdCode(userCertificate));
        return signatoryService.select(Long.parseLong(nationalRegistrationNumber));
    }


}
