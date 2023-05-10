package io.github.saxomoose.sca.web.services;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.Digest;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.spi.DSSUtils;
import io.github.saxomoose.sca.web.controllers.pki.Signature;
import io.github.saxomoose.sca.web.controllers.pki.SignatureCertificate;
import io.github.saxomoose.sca.web.utils.Tuple;

@Service
public class PKISignatureService {
    private final PAdESService padesService;

    @Autowired
    public PKISignatureService(PAdESService padesService) {
        this.padesService = padesService;
    }

    public Tuple<io.github.saxomoose.sca.web.controllers.pki.Digest, PAdESSignatureParameters> dataToSign(
        final File file,
        final SignatureCertificate signatureCertificate) throws CertificateException, NoSuchAlgorithmException 
    {
        final var x509Certificate = signatureCertificate.toX509Certificate();
        // final var digestAlgorithm = signatureCertificate.supportedAlgorithmNames().contains("SHA-384") ? DigestAlgorithm.SHA384 : DigestAlgorithm.SHA256;
        final var parameters = new PAdESSignatureParameters();
        // Double reserved space for signature. https://ec.europa.eu/digital-building-blocks/tracker/browse/DSS-3030
        // parameters.setContentSize(9472 * 2);
        parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
        parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
        final var certificateToken = new CertificateToken(x509Certificate);
        parameters.setSigningCertificate(certificateToken);
        // parameters.setCertificateChain(null);
        final var toSignDocument = new FileDocument(file);
        final var toBeSigned = padesService.getDataToSign(toSignDocument, parameters);
        final var digest = new Digest(
            DigestAlgorithm.SHA256, 
            DSSUtils.digest(DigestAlgorithm.SHA256, toBeSigned.getBytes()));
        final var base64Digest = Base64.getEncoder().encodeToString(digest.getValue());
        return Tuple.of(
            new io.github.saxomoose.sca.web.controllers.pki.Digest(
                base64Digest, 
                DigestAlgorithm.SHA256.getJavaName()),
            parameters);
    }

    public DSSDocument applySignature(
        final File file, 
        final PAdESSignatureParameters parameters, final Signature signature) throws CertificateException
    {   
        final var toSignDocument = new FileDocument(file);
        final var signatureBytes = Base64.getDecoder().decode(signature.base64Signature());
        final var signatureValue = new SignatureValue(SignatureAlgorithm.RSA_SHA256, signatureBytes);
        return padesService.signDocument(toSignDocument, parameters, signatureValue);
    }
}
