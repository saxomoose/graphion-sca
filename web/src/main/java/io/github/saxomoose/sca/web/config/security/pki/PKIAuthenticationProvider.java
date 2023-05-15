package io.github.saxomoose.sca.web.config.security.pki;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import eu.webeid.security.certificate.CertificateData;
import eu.webeid.security.challenge.ChallengeNonceStore;
import eu.webeid.security.validator.AuthTokenValidator;
import io.github.saxomoose.sca.web.exceptions.IncorrectNRNException;
import io.github.saxomoose.sca.web.models.User;
import io.github.saxomoose.sca.web.services.SignatoryService;
import io.github.saxomoose.sca.web.services.UserService;

/**
 * Custom authentication provider that uses the authentication token validator for authentication.
 */
@Component
public class PKIAuthenticationProvider implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(PKIAuthenticationProvider.class);

    private final AuthTokenValidator authTokenValidator;
    private final ChallengeNonceStore challengeNonceStore;
    private final PasswordEncoder encoder;
    private final SignatoryService signatoryService;
    private final UserService userService;

    @Autowired
    public PKIAuthenticationProvider(
        AuthTokenValidator authTokenValidator,
        ChallengeNonceStore challengeNonceStore,
        PasswordEncoder encoder,
        SignatoryService signatoryService,
        UserService userService) 
    {
        this.authTokenValidator = authTokenValidator;
        this.challengeNonceStore = challengeNonceStore;
        this.encoder = encoder;
        this.signatoryService = signatoryService;
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException 
    {
        logger.info(String.format("authenticate(): %s", auth));
        final var authentication = (PreAuthenticatedAuthenticationToken) auth;
        final var authenticationPayload = ((AuthenticationPayload) authentication.getCredentials());
        try {
            final var nonce = challengeNonceStore.getAndRemove().getBase64EncodedNonce();
            final var userCertificate = authTokenValidator.validate(authenticationPayload.token(), nonce);
            final var user = userFromCertificate(authenticationPayload.username(), userCertificate);
            return PKIAuthentication.of(user);
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

    private User userFromCertificate(String username, X509Certificate userCertificate) throws AuthenticationException, CertificateEncodingException 
    {
        final var user = userService.select(username);
        final var rawNRN = Objects.requireNonNull(CertificateData.getSubjectIdCode(userCertificate));
        if(!encoder.matches(rawNRN, user.nrn())) {
            throw new IncorrectNRNException("TODO");
        }
        return user;
        // final var uuid = UUID.nameUUIDFromBytes(nationalRegistrationNumber.getBytes());
        // if (Target.valueOf(target) == Target.TENANT) {
        //     return userService.select(uuid);
        // } else if (Target.valueOf(target) == Target.CLOSING) {
        //     return signatoryService.select(uuid);
        // }
        // Unreachable.
    }
}
