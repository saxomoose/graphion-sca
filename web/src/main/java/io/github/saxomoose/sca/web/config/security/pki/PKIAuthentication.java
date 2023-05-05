package io.github.saxomoose.sca.web.config.security.pki;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import eu.webeid.security.certificate.CertificateData;

public class PKIAuthentication extends PreAuthenticatedAuthenticationToken {
    private static final Logger logger = LoggerFactory.getLogger(PKIAuthentication.class);

    private final String idCode;

    public static Authentication of(X509Certificate userCertificate, List<GrantedAuthority> authorities) throws CertificateEncodingException 
    {
        final var principalName = principalNameFromCertificate(userCertificate);
        final var idCode = Objects.requireNonNull(CertificateData.getSubjectIdCode(userCertificate));
        return new PKIAuthentication(principalName, idCode, authorities);
    }

    public String idCode() 
    {
        return idCode;
    }

    private PKIAuthentication(String principalName, String idCode, List<GrantedAuthority> authorities) 
    {
        super(principalName, idCode, authorities);
        this.idCode = idCode;
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
