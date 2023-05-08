package io.github.saxomoose.sca.web.controllers.pki;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;

public class SignatureCertificate {
    private String certificate;
    private List<SignatureAlgorithm> supportedSignatureAlgorithms;
    
    public String certificate()
    {
        return certificate;
    }

    public List<SignatureAlgorithm> supportedSignatureAlgorithms()
    {
        return supportedSignatureAlgorithms;
    }

    public void setCertificate(String certificate)
    {
        this.certificate = certificate;
    }

    public void setSupportedSignatureAlgorithms(List<SignatureAlgorithm> supportedSignatureAlgorithms)
    {
        this.supportedSignatureAlgorithms = supportedSignatureAlgorithms;
    }

    public X509Certificate toX509Certificate() throws CertificateException {
        final var certificateBytes = Base64.getDecoder().decode(certificate);
        final var inputStream = new ByteArrayInputStream(certificateBytes);
        final var certificateFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certificateFactory.generateCertificate(inputStream);
    }

    public List<String> supportedAlgorithmNames() {
        return supportedSignatureAlgorithms
            .stream()
            .map(SignatureAlgorithm::hashAlgorithm)
            .toList();
    }

    
}