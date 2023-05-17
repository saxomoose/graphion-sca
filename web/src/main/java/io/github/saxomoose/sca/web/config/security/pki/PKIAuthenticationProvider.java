package io.github.saxomoose.sca.web.config.security.pki;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import eu.webeid.security.certificate.CertificateData;
import eu.webeid.security.challenge.ChallengeNonceStore;
import eu.webeid.security.validator.AuthTokenValidator;
import io.github.saxomoose.sca.web.enums.Role;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.models.User;
import io.github.saxomoose.sca.web.services.TenantService;
import io.github.saxomoose.sca.web.services.UserService;
import io.github.saxomoose.sca.web.utils.Tuple;

/**
 * Custom authentication provider that uses the authentication token validator for authentication.
 */
@Component
public class PKIAuthenticationProvider implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(PKIAuthenticationProvider.class);

    private final AuthTokenValidator authTokenValidator;
    private final ChallengeNonceStore challengeNonceStore;
    private final TenantService tenantService;
    private final UserService userService;

    @Autowired
    public PKIAuthenticationProvider(
        AuthTokenValidator authTokenValidator,
        ChallengeNonceStore challengeNonceStore,
        TenantService tenantService,
        UserService userService) 
    {
        this.authTokenValidator = authTokenValidator;
        this.challengeNonceStore = challengeNonceStore;
        this.tenantService = tenantService;
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
            final var defaultTenant = defaultTenant(user);
            if(defaultTenant != null) {
                return PKIAuthentication.of(user, defaultTenant.first(), defaultTenant.second());
            } else {
                return PKIAuthentication.of(user);
            }
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

    /**
     * This method uses case-sensitive string comparison.
     * Only the first first name is used for comparison.
     */
    private User userFromCertificate(String username, X509Certificate userCertificate) throws AuthenticationException, CertificateEncodingException 
    {
        final var user = userService.select(username);
        final var countryCode = Objects.requireNonNull(CertificateData.getSubjectCountryCode(userCertificate));
        final var rawFirstName = Objects.requireNonNull(CertificateData.getSubjectGivenName(userCertificate));
        final var firstName = rawFirstName.substring(0, rawFirstName.indexOf(" "));
        final var lastName = Objects.requireNonNull(CertificateData.getSubjectSurname(userCertificate));
        final var nrn = Objects.requireNonNull(CertificateData.getSubjectIdCode(userCertificate));
        final var rawBirthYear = Integer.parseInt(nrn.substring(0, 2));
        final var birthMonth = Integer.parseInt(nrn.substring(2, 4));
        final var birthDay = Integer.parseInt(nrn.substring(4, 6));
        final var numberToCheck = Long.parseLong(String.format("%d%s", 2, nrn.substring(0, 9)));
        final var controlNumber = Integer.parseInt(nrn.substring(9, 11));
        final var bornAfter2000 = (97 - (numberToCheck % 97)) == controlNumber;
        LocalDate dateOfBirth;
        if(bornAfter2000) {
            dateOfBirth = LocalDate.of(
                Integer.parseInt(String.format("%d%d", 20, rawBirthYear)),
                birthMonth,
                birthDay);
        } else {
            dateOfBirth = LocalDate.of(
                Integer.parseInt(String.format("%d%d", 19, rawBirthYear)),
                birthMonth,
                birthDay);
        }
        if (
            countryCode.equals("BE") 
            && firstName.equals(user.firstName())
            && lastName.equals(user.lastName())
            && dateOfBirth.isEqual(user.dateOfBirth())
        ) {
            return user;
        } else {
            throw new BadCredentialsException("TODO");
        }
    }

    private Tuple<Tenant, Role> defaultTenant(User user)
    {
        return tenantService.defaultTenant(user.id());
    }
}
