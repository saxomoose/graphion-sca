package io.github.saxomoose.sca.web.config.security;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import eu.webeid.security.certificate.CertificateData;
import eu.webeid.security.challenge.ChallengeNonceStore;
import eu.webeid.security.validator.AuthTokenValidator;
import io.github.saxomoose.sca.web.enums.RoleEnum;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.models.User;
import io.github.saxomoose.sca.web.services.TenantService;
import io.github.saxomoose.sca.web.services.UserService;
import io.github.saxomoose.sca.web.utils.Tuple;

/**
 * Custom authentication provider that uses the authentication token validator for authentication.
 */
@Component
public class PkiAuthenticationProvider implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(PkiAuthenticationProvider.class);

    private final AuthTokenValidator authTokenValidator;
    private final ChallengeNonceStore challengeNonceStore;
    private final TenantService tenantService;
    private final UserService userService;

    @Autowired
    public PkiAuthenticationProvider(
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
    public Authentication authenticate(final Authentication auth) throws AuthenticationException 
    {
        logger.info(String.format("authenticate(): %s", auth));
        final PreAuthenticatedAuthenticationToken authentication = (PreAuthenticatedAuthenticationToken) auth;
        final AuthenticationPayload authenticationPayload = ((AuthenticationPayload) authentication.getCredentials());
        try {
            final String nonce = challengeNonceStore.getAndRemove().getBase64EncodedNonce();
            final X509Certificate userCertificate = authTokenValidator.validate(authenticationPayload.token(), nonce);
            final User user = userFromCertificate(authenticationPayload.username(), userCertificate);
            final Tuple<Tenant, List<GrantedAuthority>> tuple = defaultTenant(user);
            if (tuple != null) {
                return PkiAuthentication.of(user, (Tenant) tuple.first(), (List<GrantedAuthority>) tuple.second());
            } else {
                return PkiAuthentication.of(user, List.of(new SimpleGrantedAuthority(RoleEnum.ROLE_NULL.toString())));
            }
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    @Override
    public boolean supports(final Class<?> authentication) 
    {
        return PreAuthenticatedAuthenticationToken.class.equals(authentication);
    }

    /**
     * This method uses case-sensitive string comparison.
     * Only the first first name is used for comparison.
     * TODO: only accept Belgian id cards
     */
    private User userFromCertificate(
        final String username, 
        final X509Certificate userCertificate) throws AuthenticationException, CertificateEncodingException 
    {
        final User user = userService.userByUsername(username);
        final String issuerCountryCode = issuerCountryCode(userCertificate);
        final String rawFirstName = Objects.requireNonNull(CertificateData.getSubjectGivenName(userCertificate));
        final String firstName = rawFirstName.contains(" ") ?
             rawFirstName.substring(0, rawFirstName.indexOf(" ")) 
             : rawFirstName;
        final String lastName = Objects.requireNonNull(CertificateData.getSubjectSurname(userCertificate));
        final String nrn = Objects.requireNonNull(CertificateData.getSubjectIdCode(userCertificate));
        final Integer birthYear = Integer.parseInt(nrn.substring(0, 2));
        final Integer birthMonth = Integer.parseInt(nrn.substring(2, 4));
        final Integer birthDay = Integer.parseInt(nrn.substring(4, 6));
        LocalDate dateOfBirth = null;
        if(bornAfter2000(nrn)) {
            dateOfBirth = LocalDate.of(Integer.parseInt(String.format("%d%d", 20, birthYear)), birthMonth, birthDay);
        } else {
            dateOfBirth = LocalDate.of(Integer.parseInt(String.format("%d%d", 19, birthYear)), birthMonth, birthDay);
        }
        if (issuerCountryCode.equals("BE")
            && firstName.equals(user.firstName())
            && lastName.equals(user.lastName())
            && dateOfBirth.isEqual(user.dateOfBirth())
        ) {
            return user;
        } else {
            throw new BadCredentialsException("TODO");
        }
    }

    private Tuple<Tenant, List<GrantedAuthority>> defaultTenant(final User user)
    {
        return tenantService.tenantWithUserAuthority(user.id(), Boolean.TRUE);
    }

    private String issuerCountryCode(final X509Certificate userCertificate)
    {
        final String issuerName = userCertificate.getIssuerX500Principal().getName();
        String countryCode = null;
        final String[] parts = issuerName.split(",");
        for (String part : parts) {
            if (part.trim().startsWith("C=")) {
                countryCode = part.trim().substring(2);
                break;
            }
        }
        return countryCode;
    }

    private Boolean bornAfter2000(final String nrn)
    {
         final Long numberToCheck = Long.parseLong(String.format("%d%s", 2, nrn.substring(0, 9)));
        final Integer controlNumber = Integer.parseInt(nrn.substring(9, 11));
        return (97 - (numberToCheck % 97)) == controlNumber;
    }

}
