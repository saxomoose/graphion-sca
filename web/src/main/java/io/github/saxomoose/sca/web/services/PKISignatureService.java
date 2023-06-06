package io.github.saxomoose.sca.web.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.Digest;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.spi.DSSUtils;
import io.github.saxomoose.sca.web.controllers.pki.SignatureCertificatePayload;
import io.github.saxomoose.sca.web.controllers.pki.SignaturePayload;
import io.github.saxomoose.sca.web.enums.Path;
import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;
import io.github.saxomoose.sca.web.utils.Tuple;

@Service
public class PKISignatureService {
    private final MinIOService minioService;
    private final PAdESService padesService;
    private final RedisService redisService;
    private final ResourceLoader resourceLoader;

    @Autowired
    public PKISignatureService(
        MinIOService minioService, 
        PAdESService padesService, 
        RedisService redisService, 
        ResourceLoader resourceLoader) {
        this.minioService = minioService;
        this.padesService = padesService;
        this.redisService = redisService;
        this.resourceLoader = resourceLoader;
    }

    public Tuple<io.github.saxomoose.sca.web.controllers.pki.DigestPayload, PAdESSignatureParameters> dataToSign(
        final String bucket,
        final UUID fileUUID,
        final SignatureCertificatePayload signatureCertificate) throws CertificateException, NoSuchAlgorithmException, IOException, BusinessLogicException 
    {
        // redisService.lockFile(fileUUID);
        final var inputStream = minioService.objectAsStream(
            bucket, 
            String.format("/%s/%s", Path.CLOSING_DOCUMENTS, fileUUID));
        final var path = String.format(
            "%s/%s/%s/%s",
            System.getProperty("user.dir"), 
            Path.DSS_WORKBENCH,
            bucket, 
            fileUUID);
        final var file = new File(path);
        file.getParentFile().mkdirs();
        Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

        final var x509Certificate = signatureCertificate.toX509Certificate();
        final var certificateToken = new CertificateToken(x509Certificate);
        final var parameters = new PAdESSignatureParameters();
        parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_T);
        parameters.setSigningCertificate(certificateToken);
        parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
        
        final var toSignDocument = new FileDocument(file);
        final ToBeSigned toBeSigned = padesService.getDataToSign(toSignDocument, parameters);
        final var digest = new Digest(
            DigestAlgorithm.SHA256, 
            DSSUtils.digest(DigestAlgorithm.SHA256, toBeSigned.getBytes()));
        final String base64Digest = Base64.getEncoder().encodeToString(digest.getValue());

        return Tuple.of(
            new io.github.saxomoose.sca.web.controllers.pki.DigestPayload(
                base64Digest, 
                DigestAlgorithm.SHA256.getJavaName()),
            parameters);
    }

    public DSSDocument applySignature(
        final String bucket,
        final UUID fileUUID, 
        final PAdESSignatureParameters parameters,
        final SignaturePayload signature) throws CertificateException, IOException
    {   
        
        final var path = String.format(
            "%s:%s/%s/%s/%s",
            "file",
            System.getProperty("user.dir"),
            Path.DSS_WORKBENCH,
            bucket,
            fileUUID);
        final var file = resourceLoader.getResource(path).getFile();

        final var toSignDocument = new FileDocument(file);
        final ToBeSigned toBeSigned = padesService.getDataToSign(toSignDocument, parameters);
        final byte[] signatureBytes = Base64.getDecoder().decode(signature.base64Signature());
        final var signatureValue = new SignatureValue(SignatureAlgorithm.RSA_SHA256, signatureBytes);
        Assert.isTrue(
            padesService.isValidSignatureValue(
                toBeSigned, 
                signatureValue, 
                parameters.getSigningCertificate()), 
            "The signature value computed client-side is invalid.");
        final DSSDocument signedDocument = padesService.signDocument(toSignDocument, parameters, signatureValue);
        
        parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_LT);
        final var extendedDocument = padesService.extendDocument(signedDocument, parameters);

        parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_LTA);
        final var ltaExtendedDocument = padesService.extendDocument(extendedDocument, parameters);
 
        minioService.storeObject(
            bucket, 
            String.format("/%s/%s", Path.SIGNED, fileUUID),
            signedDocument.openStream());
        minioService.storeObject(
            bucket, 
            String.format("/%s/%s-%s", Path.SIGNED, fileUUID, "extended-lt"),
            extendedDocument.openStream());
        minioService.storeObject(
            bucket, 
            String.format("/%s/%s-%s", Path.SIGNED, fileUUID, "extended-lta"),
            ltaExtendedDocument.openStream());
        file.delete();
        final var parentPath = Paths.get(file.getAbsolutePath()).getParent();
        if (!Files.list(parentPath).findFirst().isPresent()) {
            Files.delete(parentPath);
        }
        //redisService.unlockFile(fileUUID);
        
        return extendedDocument;
    }
}
