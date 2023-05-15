package io.github.saxomoose.sca.web.config.security.pki;

import java.security.cert.X509Certificate;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import eu.webeid.security.certificate.CertificateData;
import io.github.saxomoose.sca.web.models.User;

public class PKIAuthentication extends PreAuthenticatedAuthenticationToken {
    private static final Logger logger = LoggerFactory.getLogger(PKIAuthentication.class);

    /**
     * TODO: necessary? Parent works without it.
     */
    // private final UUID subjectIdCode;

    public static Authentication of(User user)
    {
        return new PKIAuthentication(user);
    }

    // public UUID subjectIdCode() 
    // {
    //     return subjectIdCode;
    // }

    private PKIAuthentication(User principal) 
    {
        // TODO: user.authorities(). Refresh authorities later on.
        super(principal, null, null);
        // this.subjectIdCode = subjectIdCode;
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
