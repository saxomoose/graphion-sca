package io.github.saxomoose.sca.web.controllers.pki;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.support.SessionFlashMapManager;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import io.github.saxomoose.sca.web.enums.Path;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.services.MinIOService;
import io.github.saxomoose.sca.web.services.PKISignatureService;
import io.github.saxomoose.sca.web.services.ValidationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
public class SignatureController {
    private final HttpSession httpSession;
    private final MinIOService minioService;
    private final PKISignatureService signatureService;
    private final ResourceLoader resourceLoader;
    private final ValidationService validationService;

    public SignatureController(
        HttpSession httpSession, 
        MinIOService minioService, 
        PKISignatureService signatureService,
        ResourceLoader resourceLoader,
        ValidationService validationService) {
        this.httpSession = httpSession;
        this.minioService = minioService;
        this.signatureService = signatureService;
        this.resourceLoader = resourceLoader;
        this.validationService = validationService;
    }


    @PostMapping(value = "/pki/sign/dtbsr/{rawFileUUID}", produces = "application/json")
    public ResponseEntity<Digest> dataToSign(
        HttpServletRequest request,
        HttpServletResponse response,
        @PathVariable final String rawFileUUID,
        @RequestBody final SignatureCertificate signatureCertificate
    ) throws CertificateException, NoSuchAlgorithmException, IOException
    {
        final var tenant = ((Tenant) httpSession.getAttribute("tenant"));
        final var fileUUID = validationService.uuid(rawFileUUID);
        final var inputStream = minioService.objectAsStream(
            "graphion", 
            String.format("%s%s", Path.UPLOADS, rawFileUUID));
        final var path = String.format("%s%s%s", System.getProperty("user.dir"), Path.WORKBENCH, fileUUID);
        final var file = new File(path);
        Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        final var tuple = signatureService.dataToSign(file, signatureCertificate);
        final var flashMapManager = new SessionFlashMapManager();
        final var flashMap = new FlashMap();
        flashMap.put("parameters", tuple.parameters());
        flashMapManager.saveOutputFlashMap(flashMap, request, response);
        return ResponseEntity.ok().body(tuple.digest());
    }

    @PostMapping(value = "/pki/sign/apply/{rawFileUUID}", produces = "application/json")
    public ResponseEntity sign(
        HttpServletRequest request,
        @PathVariable final String rawFileUUID,
        @RequestBody final Signature signature
    ) throws CertificateException, IOException
    {
        final var fileUUID = validationService.uuid(rawFileUUID);
        final var inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        final var parameters = (PAdESSignatureParameters) inputFlashMap.get("parameters");
        final var path = String.format("%s%s%s", System.getProperty("user.dir"), Path.WORKBENCH, fileUUID);
        final var file = resourceLoader.getResource(String.format("%s%s", "file:", path)).getFile();
        final var signedDocument = signatureService.applySignature(file, parameters, signature);
        minioService.storeFile(
            "graphion", 
            String.format("%s%s", Path.SIGNED, fileUUID),
            signedDocument.openStream());
        file.delete();
        return ResponseEntity.noContent().build();
    }
}
