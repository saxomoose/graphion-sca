package io.github.saxomoose.sca.web.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
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
import eu.europa.esig.dss.signature.resources.TempFileResourcesHandlerBuilder;
import eu.europa.esig.dss.spi.DSSUtils;
import io.github.saxomoose.sca.web.controllers.pki.DigestPayload;
import io.github.saxomoose.sca.web.controllers.pki.SignatureCertificatePayload;
import io.github.saxomoose.sca.web.controllers.pki.SignaturePayload;
import io.github.saxomoose.sca.web.enums.PathEnum;
import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;
import io.github.saxomoose.sca.web.utils.Tuple;

@Service
public class PKISignatureService {
    private final MinIOService minioService;
    private final PAdESService padesService;
    private final RedisService redisService;
    private final ResourceLoader resourceLoader;
    private final TempFileResourcesHandlerBuilder tempFileResourcesHandlerBuilder;

    @Autowired
    public PKISignatureService(
        MinIOService minioService, 
        PAdESService padesService, 
        RedisService redisService, 
        ResourceLoader resourceLoader,
        TempFileResourcesHandlerBuilder tempFileResourcesHandlerBuilder) 
    {
        this.minioService = minioService;
        this.padesService = padesService;
        this.redisService = redisService;
        this.resourceLoader = resourceLoader;
        this.tempFileResourcesHandlerBuilder = tempFileResourcesHandlerBuilder;
    }

    public Tuple<DigestPayload, PAdESSignatureParameters> dataToSign(
        final String bucket,
        final UUID fileUUID,
        final SignatureCertificatePayload signatureCertificate) 
    throws CertificateException, NoSuchAlgorithmException, IOException, BusinessLogicException 
    {
        // redisService.lockFile(fileUUID);
        final InputStream inputStream = minioService.objectAsStream(
            bucket, 
            String.format("/%s/%s", PathEnum.CLOSING_DOCUMENTS, fileUUID));
        final File file = workbenchFile(bucket, fileUUID);
        file.getParentFile().mkdirs();
        Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

        final X509Certificate x509Certificate = signatureCertificate.toX509Certificate();
        final CertificateToken certificateToken = new CertificateToken(x509Certificate);
        final PAdESSignatureParameters parameters = new PAdESSignatureParameters();
        parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_T);
        parameters.setSigningCertificate(certificateToken);
        parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
        
        final DSSDocument toSignDocument = new FileDocument(file);
        final ToBeSigned toBeSigned = padesService.getDataToSign(toSignDocument, parameters);
        final Digest digest = new Digest(
            DigestAlgorithm.SHA256, 
            DSSUtils.digest(DigestAlgorithm.SHA256, toBeSigned.getBytes()));
        final String base64Digest = Base64.getEncoder().encodeToString(digest.getValue());

        return Tuple.of(
            new DigestPayload(
                base64Digest, 
                DigestAlgorithm.SHA256.getJavaName()),
            parameters);
    }

    public File applySignature(
        final String bucket,
        final UUID fileUUID, 
        final PAdESSignatureParameters parameters,
        final SignaturePayload signature) throws CertificateException, IOException
    {    
        final File workbenchFile = workbenchFile(bucket, fileUUID);

        final DSSDocument toSignDocument = new FileDocument(workbenchFile);
        final ToBeSigned toBeSigned = padesService.getDataToSign(toSignDocument, parameters);
        final byte[] signatureBytes = Base64.getDecoder().decode(signature.base64Signature());
        final SignatureValue signatureValue = new SignatureValue(SignatureAlgorithm.RSA_SHA256, signatureBytes);
        Assert.isTrue(
            padesService.isValidSignatureValue(
                toBeSigned, 
                signatureValue, 
                parameters.getSigningCertificate()), 
            "The signature value computed client-side is invalid.");
        final DSSDocument signedDocument = padesService.signDocument(toSignDocument, parameters, signatureValue);
        Files.copy(signedDocument.openStream(), workbenchFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        tempFileResourcesHandlerBuilder.clear();

        return workbenchFile;
    }

    public void storeDocument(
        final String bucket,
        final UUID fileUUID) throws IOException
    {
        final File workbenchFile = workbenchFile(bucket, fileUUID);
        minioService.storeObject(
            bucket, 
            String.format("/%s/%s", PathEnum.SIGNED, fileUUID),
            new FileInputStream(workbenchFile));

        workbenchFile.delete();
        final Path parentPath = Paths.get(workbenchFile.getAbsolutePath()).getParent();
        if (!Files.list(parentPath).findFirst().isPresent()) {
            Files.delete(parentPath);
        }
        //redisService.unlockFile(fileUUID);
    }

    private File workbenchFile(final String bucket, final UUID fileUUID)
    {
        final String path = String.format(
            "%s:%s/%s/%s/%s",
            "file",
            System.getProperty("user.dir"),
            PathEnum.DSS_WORKBENCH,
            bucket,
            fileUUID);
        return new File(path);
    }
}
