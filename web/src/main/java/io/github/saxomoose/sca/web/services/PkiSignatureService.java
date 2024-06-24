package io.github.saxomoose.sca.web.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import eu.europa.esig.dss.signature.resources.TempFileResourcesHandlerBuilder;
import eu.europa.esig.dss.spi.DSSUtils;
import io.github.saxomoose.sca.web.enums.PathEnum;
import io.github.saxomoose.sca.web.exceptions.LogicException;
import io.github.saxomoose.sca.web.models.transfer.pki.DigestPayload;
import io.github.saxomoose.sca.web.models.transfer.pki.SignatureCertificatePayload;
import io.github.saxomoose.sca.web.models.transfer.pki.SignaturePayload;
import io.github.saxomoose.sca.web.utils.Notification;
import io.github.saxomoose.sca.web.utils.Tuple;

@Service
public class PkiSignatureService {
    private static final Logger logger = LoggerFactory.getLogger(PkiSignatureService.class);
    
    private final DocumentService documentService;
    private final ObjectStorageService objectStorageService;
    private final PAdESService padesService;
    private final RedisService redisService;
    private final TempFileResourcesHandlerBuilder tempFileResourcesHandlerBuilder;

    @Autowired
    public PkiSignatureService(
        DocumentService documentService,
        ObjectStorageService objectStorageService, 
        PAdESService padesService, 
        RedisService redisService, 
        TempFileResourcesHandlerBuilder tempFileResourcesHandlerBuilder) 
    {
        this.documentService = documentService;
        this.objectStorageService = objectStorageService;
        this.padesService = padesService;
        this.redisService = redisService;
        this.tempFileResourcesHandlerBuilder = tempFileResourcesHandlerBuilder;
    }

    public Tuple<DigestPayload, PAdESSignatureParameters> dataToSign(
        String bucket,
        UUID fileUUID,
        SignatureCertificatePayload signatureCertificate) 
    throws CertificateException, NoSuchAlgorithmException, IOException, LogicException 
    {
        redisService.lockFile(fileUUID);
        InputStream inputStream = objectStorageService.objectAsStream(
            bucket, 
            String.format("%s/%s", PathEnum.CLOSING_DOCUMENTS, fileUUID));
        File file = workbenchFile(bucket, fileUUID);
        file.getParentFile().mkdirs();
        Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

        X509Certificate x509Certificate = signatureCertificate.toX509Certificate();
        CertificateToken certificateToken = new CertificateToken(x509Certificate);
        PAdESSignatureParameters parameters = new PAdESSignatureParameters();
        parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_T);
        parameters.setSigningCertificate(certificateToken);
        parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
        
        DSSDocument toSignDocument = new FileDocument(file);
        ToBeSigned toBeSigned = padesService.getDataToSign(toSignDocument, parameters);
        Digest digest = new Digest(
            DigestAlgorithm.SHA256, 
            DSSUtils.digest(DigestAlgorithm.SHA256, toBeSigned.getBytes()));
        String base64Digest = Base64.getEncoder().encodeToString(digest.getValue());

        return new Tuple<DigestPayload, PAdESSignatureParameters>(
            new DigestPayload(base64Digest,
            DigestAlgorithm.SHA256.getJavaName()), parameters);
    }

    public DSSDocument applySignature(
        String bucket,
        UUID fileUUID, 
        PAdESSignatureParameters parameters,
        SignaturePayload signature) throws CertificateException, IOException, LogicException
    {    
        File workbenchFile = workbenchFile(bucket, fileUUID);

        DSSDocument toSignDocument = new FileDocument(workbenchFile);
        ToBeSigned toBeSigned = padesService.getDataToSign(toSignDocument, parameters);
        byte[] signatureBytes = Base64.getDecoder().decode(signature.base64Signature());
        SignatureValue signatureValue = new SignatureValue(SignatureAlgorithm.RSA_SHA256, signatureBytes);
        if(!padesService.isValidSignatureValue(toBeSigned, signatureValue, parameters.getSigningCertificate())) {
            logger.error("File {}: the signature value computed client-side is invalid.", fileUUID);
            throw new LogicException(new Notification(Notification.Type.ERROR, "TODO", "TODO"));
        } 
        FileDocument signedDocument = (FileDocument) padesService.signDocument(toSignDocument, parameters, signatureValue);
        // parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_LT);
        // FileDocument extendedLTDocument = (FileDocument) padesService.extendDocument(signedDocument, parameters);
        parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_LTA);
        FileDocument extendedLTADocument = (FileDocument) padesService.extendDocument(signedDocument, parameters);

        return extendedLTADocument;
    }

    public void cleanUp(
        String bucket,
        UUID fileUUID) throws IOException
    {
        File workbenchFile = workbenchFile(bucket, fileUUID);
        workbenchFile.delete();
        File parentFile = workbenchFile.getParentFile();
        if (!Files.list(parentFile.toPath()).findFirst().isPresent()) {
            Files.delete(parentFile.toPath());
        }
        tempFileResourcesHandlerBuilder.clear();
        redisService.unlockFile(fileUUID);
    }

    private File workbenchFile(String bucket, UUID fileUUID)
    {
        String path = String.format(
            "%s/%s/%s/%s",
            System.getProperty("user.dir"),
            PathEnum.DSS_WORKBENCH,
            bucket,
            fileUUID);
        return new File(path);
    }
}
