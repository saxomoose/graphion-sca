package io.github.saxomoose.sca.web.controllers.pki;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.services.MinIOService;
import io.github.saxomoose.sca.web.services.PKISignatureService;
import jakarta.servlet.http.HttpSession;

@RestController
public class SignatureController {

    private final HttpSession httpSession;
    private final MinIOService minioService;
    private final PKISignatureService signatureService;

    public SignatureController(HttpSession httpSession, MinIOService minioService, PKISignatureService signatureService) {
        this.httpSession = httpSession;
        this.minioService = minioService;
        this.signatureService = signatureService;
    }


    @PostMapping(value = "/pki/sign/dtbsr/{rawFileUUID}", produces = "application/json")
    public void prepare(
        @PathVariable final String rawFileUUID,
        @RequestBody final SignatureCertificate signatureCertificate
    ) throws CertificateException, NoSuchAlgorithmException
    {
        final var tenant = ((Tenant) httpSession.getAttribute("tenant"));
        final var bytes = minioService.objectAsBytes("graphion", "/uploads/dd465658-a13b-352b-8ced-ddd13920283e");
        signatureService.getDataToSign(signatureCertificate, bytes);
    }

    @PostMapping(value = "/pki/sign/sign", produces = "application/json")
    public void sign()
    {

    }
}
