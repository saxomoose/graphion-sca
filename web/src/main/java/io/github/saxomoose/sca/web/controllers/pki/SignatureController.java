package io.github.saxomoose.sca.web.controllers.pki;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.support.SessionFlashMapManager;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import io.github.saxomoose.sca.web.config.security.PkiAuthentication;
import io.github.saxomoose.sca.web.enums.SessionEnum;
import io.github.saxomoose.sca.web.exceptions.LogicException;
import io.github.saxomoose.sca.web.models.Closing;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.models.User;
import io.github.saxomoose.sca.web.services.ClosingService;
import io.github.saxomoose.sca.web.services.PkiSignatureService;
import io.github.saxomoose.sca.web.services.PkiValidationService;
import io.github.saxomoose.sca.web.services.ValidationService;
import io.github.saxomoose.sca.web.utils.Tuple;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
public class SignatureController {
    private final ClosingService closingService;
    private final HttpSession httpSession;
    private final PkiSignatureService pkiSignatureService;
    private final PkiValidationService pkiValidationService;
    private final ValidationService validationService;

    public SignatureController(
        ClosingService closingService,
        HttpSession httpSession, 
        PkiSignatureService pkiSignatureService,
        PkiValidationService pkiValidationService,
        ValidationService validationService) 
    {
        this.closingService = closingService;
        this.httpSession = httpSession;
        this.pkiSignatureService = pkiSignatureService;
        this.pkiValidationService = pkiValidationService;
        this.validationService = validationService;
    }

    @PostMapping(
        value = "/pki/sign/dtbsr/{rawClosingId}/{rawFileUUID}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DigestPayload> dataToSign(
        HttpServletRequest request,
        HttpServletResponse response,
        @PathVariable final Long rawClosingId,
        @PathVariable final String rawFileUUID,
        @RequestBody final SignatureCertificatePayload signatureCertificate
    ) throws CertificateException, NoSuchAlgorithmException, IOException, LogicException
    {
        final Long userId = ((User) ((PkiAuthentication) request.getUserPrincipal()).getPrincipal()).id();
        final String bucket = ((Tenant) httpSession.getAttribute(SessionEnum.TENANT.toString())).bucket();
        final UUID fileUUID = validationService.uuid(rawFileUUID);
        closingService.closingIsExpired(rawClosingId);
        closingService.userStatusOnClosing(rawClosingId, userId, fileUUID);
        final Tuple<DigestPayload, PAdESSignatureParameters> tuple = 
            pkiSignatureService.dataToSign(bucket, fileUUID, signatureCertificate);
        final FlashMapManager flashMapManager = new SessionFlashMapManager();
        final FlashMap flashMap = new FlashMap();
        flashMap.put("parameters", (PAdESSignatureParameters) tuple.second());
        flashMapManager.saveOutputFlashMap(flashMap, request, response);
        return ResponseEntity.ok()
            .body((DigestPayload) tuple.first());
    }

    @PostMapping(
        value = "/pki/sign/apply/{rawClosingId}/{rawFileUUID}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity sign(
        HttpServletRequest request,
        @PathVariable final Long rawClosingId,
        @PathVariable final String rawFileUUID,
        @RequestBody final SignaturePayload signature
    ) throws CertificateException, IOException, LogicException
    {
        final Long userId = ((User) ((PkiAuthentication) request.getUserPrincipal()).getPrincipal()).id();
        final String bucket = ((Tenant) httpSession.getAttribute(SessionEnum.TENANT.toString())).bucket();
        final UUID fileUUID = validationService.uuid(rawFileUUID);
        final Closing closing = closingService.closingById(rawClosingId);
        final Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        final PAdESSignatureParameters parameters = (PAdESSignatureParameters) inputFlashMap.get("parameters");
        final DSSDocument signedDocument = pkiSignatureService.applySignature(bucket, fileUUID, parameters, signature);
        final Instant signatureTimestamp = pkiValidationService.validateSignature(fileUUID, parameters, signedDocument);
        closingService.storeDocument(closing, userId, bucket, fileUUID, signedDocument, signatureTimestamp);
        pkiSignatureService.cleanUp(bucket, fileUUID);
        return ResponseEntity.noContent().build();
    }
}
