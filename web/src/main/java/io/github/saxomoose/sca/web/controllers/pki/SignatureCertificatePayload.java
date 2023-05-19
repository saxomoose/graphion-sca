package io.github.saxomoose.sca.web.controllers.pki;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SignatureCertificatePayload {
    private String signatureCertificate;
    private List<SignatureAlgorithm> supportedSignatureAlgorithms;
    
    @JsonProperty("certificate")
    public String signatureCertificate()
    {
        return signatureCertificate;
    }

    public List<SignatureAlgorithm> supportedSignatureAlgorithms()
    {
        return supportedSignatureAlgorithms;
    }

    public void setSignatureCertificate(String certificate)
    {
        this.signatureCertificate = certificate;
    }

    public void setSupportedSignatureAlgorithms(List<SignatureAlgorithm> supportedSignatureAlgorithms)
    {
        this.supportedSignatureAlgorithms = supportedSignatureAlgorithms;
    }

    public X509Certificate toX509Certificate() throws CertificateException {
        final byte[] certificateBytes = Base64.getDecoder().decode(signatureCertificate);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(certificateBytes);
        final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certificateFactory.generateCertificate(inputStream);
    }

    public List<String> supportedAlgorithmNames() {
        return supportedSignatureAlgorithms
            .stream()
            .map(SignatureAlgorithm::hashAlgorithm)
            .toList();
    }

    
}