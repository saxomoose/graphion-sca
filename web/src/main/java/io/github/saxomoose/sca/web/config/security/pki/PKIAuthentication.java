package io.github.saxomoose.sca.web.config.security.pki;

import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import eu.webeid.security.certificate.CertificateData;
import io.github.saxomoose.sca.web.models.Authenticable;

public class PKIAuthentication extends PreAuthenticatedAuthenticationToken {
    private static final Logger logger = LoggerFactory.getLogger(PKIAuthentication.class);

    /**
     * TODO: necessary? Parent works without it.
     */
    private final UUID subjectIdCode;

    public static Authentication of(Authenticable authenticable)
    {
        final var subjectIdCode = authenticable.uuid();
        return new PKIAuthentication(authenticable, subjectIdCode);
    }

    public UUID subjectIdCode() 
    {
        return subjectIdCode;
    }

    private PKIAuthentication(Authenticable principal, UUID subjectIdCode) 
    {
        super(principal, null, principal.role());
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
