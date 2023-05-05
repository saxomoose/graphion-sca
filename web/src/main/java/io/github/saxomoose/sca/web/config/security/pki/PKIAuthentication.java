package io.github.saxomoose.sca.web.config.security.pki;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import eu.webeid.security.certificate.CertificateData;
import io.github.saxomoose.sca.web.models.Signatory;

public class PKIAuthentication extends PreAuthenticatedAuthenticationToken {
    private static final Logger logger = LoggerFactory.getLogger(PKIAuthentication.class);

    /**
     * TODO: necessary?
     */
    private final Long subjectIdCode;

    public static Authentication of(Signatory signatory, List<GrantedAuthority> authorities)
    {
        final var subjectIdCode = signatory.nationalRegistrationNumber();
        return new PKIAuthentication(signatory, subjectIdCode, authorities);
    }

    public Long subjectIdCode() 
    {
        return subjectIdCode;
    }

    private PKIAuthentication(Signatory principal, Long subjectIdCode, List<GrantedAuthority> authorities) 
    {
        super(principal, null, authorities);
        this.subjectIdCode = subjectIdCode;
    }

    private static String principalNameFromCertificate(X509Certificate userCertificate) 
    {
        try {
            return String.format(
                "%s %s", 
                Objects.requireNonNull(CertificateData.getSubjectGivenName(userCertificate)), 
                Objects.requireNonNull(CertificateData.getSubjectSurname(userCertificate)));
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }
}
