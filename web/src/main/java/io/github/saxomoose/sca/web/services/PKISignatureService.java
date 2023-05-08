package io.github.saxomoose.sca.web.services;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import io.github.saxomoose.sca.web.controllers.pki.Digest;
import io.github.saxomoose.sca.web.controllers.pki.SignatureCertificate;

@Service
public class PKISignatureService {

    private final PAdESService padesService;

    @Autowired
    public PKISignatureService(PAdESService padesService) {
        this.padesService = padesService;
    }

    public Digest getDataToSign(final SignatureCertificate signatureCertificate, final byte[] bytes) throws CertificateException, NoSuchAlgorithmException {
        final var x509Certificate = signatureCertificate.toX509Certificate();
        final var digestAlgorithm = signatureCertificate.supportedAlgorithmNames().contains("SHA-384") ? "SHA-384" : "SHA-256";
        final var parameters = new PAdESSignatureParameters();
        // Double reserved space for signature.
        // parameters.setContentSize(9472 * 2);
        final var toSignDocument = new InMemoryDocument(bytes);
        final var toBeSigned = padesService.getDataToSign(toSignDocument, parameters);
        // final var base64EncodedDigest = Base64.getEncoder().encodeToString(MessageDigest.getInstance(digestAlgorithm).digest(bytes));
        // final var toSignDigest = new DigestDocument(DigestAlgorithm.SHA384, base64EncodedDigest, "20230308-seed-document");
        // final var toBeSigned = padesService.getDataToSign(toSignDigest, parameters);

        return new Digest();

    }
    
}
