package io.github.saxomoose.sca.web.controllers.pki;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.support.SessionFlashMapManager;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.services.PKISignatureService;
import io.github.saxomoose.sca.web.services.PKIValidationService;
import io.github.saxomoose.sca.web.services.ValidationService;
import io.github.saxomoose.sca.web.utils.Tuple;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
public class SignatureController {
    private final HttpSession httpSession;
    private final PKISignatureService pkiSignatureService;
    private final PKIValidationService pkiValidationService;
    private final ValidationService validationService;

    public SignatureController(
        HttpSession httpSession, 
        PKISignatureService pkiSignatureService,
        PKIValidationService pkiValidationService,
        ValidationService validationService) 
    {
        this.httpSession = httpSession;
        this.pkiSignatureService = pkiSignatureService;
        this.pkiValidationService = pkiValidationService;
        this.validationService = validationService;
    }

    @PostMapping(value = "/pki/sign/dtbsr/{rawClosingId}/{rawFileUUID}", produces = "application/json")
    public ResponseEntity<DigestPayload> dataToSign(
        HttpServletRequest request,
        HttpServletResponse response,
        @PathVariable final String rawFileUUID,
        @RequestBody final SignatureCertificatePayload signatureCertificate
    ) throws CertificateException, NoSuchAlgorithmException, IOException, BusinessLogicException
    {
        final var bucket = ((Tenant) httpSession.getAttribute("tenant")).bucket();
        final UUID fileUUID = validationService.uuid(rawFileUUID);
        final Tuple<DigestPayload, PAdESSignatureParameters> tuple = pkiSignatureService.dataToSign(bucket, fileUUID, signatureCertificate);
        final var flashMapManager = new SessionFlashMapManager();
        final var flashMap = new FlashMap();
        flashMap.put("parameters", tuple.second());
        flashMapManager.saveOutputFlashMap(flashMap, request, response);
        return ResponseEntity.ok().body(tuple.first());
    }

    @PostMapping(value = "/pki/sign/apply/{rawClosingId}/{rawFileUUID}", produces = "application/json")
    public ResponseEntity sign(
        HttpServletRequest request,
        @PathVariable final String rawFileUUID,
        @RequestBody final SignaturePayload signature
    ) throws CertificateException, IOException
    {
        final var bucket = ((Tenant) httpSession.getAttribute("tenant")).bucket();
        final UUID fileUUID = validationService.uuid(rawFileUUID);
        final var inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        final var parameters = (PAdESSignatureParameters) inputFlashMap.get("parameters");
        final DSSDocument signedDocument = pkiSignatureService.applySignature(bucket, fileUUID, parameters, signature);
        pkiValidationService.validateSignature(signedDocument);
        return ResponseEntity.noContent().build();
    }
}
