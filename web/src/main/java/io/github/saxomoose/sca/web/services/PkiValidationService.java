package io.github.saxomoose.sca.web.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import eu.europa.esig.dss.validation.reports.Reports;
import io.github.saxomoose.sca.web.exceptions.LogicException;
import io.github.saxomoose.sca.web.utils.Notification;

@Service
public class PkiValidationService {
    private static final Logger logger = LoggerFactory.getLogger(RedisSubService.class);

    private final CertificateVerifier certificateVerifier;

    @Autowired
    public PkiValidationService(CertificateVerifier certificateVerifier)
    {
        this.certificateVerifier = certificateVerifier;
    }

    public Instant validateSignature(
        final UUID fileUUID,
        final PAdESSignatureParameters parameters, 
        final DSSDocument signedDocument) throws LogicException
    {
        final var documentValidator = SignedDocumentValidator.fromDocument(signedDocument);
        documentValidator.setCertificateVerifier(certificateVerifier);
        if (parameters.getSignatureLevel().equals(SignatureLevel.PAdES_BASELINE_T)) {
            documentValidator.setValidationLevel(ValidationLevel.LONG_TERM_DATA);
        } else if (parameters.getSignatureLevel().equals(SignatureLevel.PAdES_BASELINE_LTA)) {
            documentValidator.setValidationLevel(ValidationLevel.ARCHIVAL_DATA);
        }
        final Reports reports = documentValidator.validateDocument();
        if (!(reports.getSimpleReport().getSignaturesCount() == reports.getSimpleReport().getValidSignaturesCount())) {
            logger.error("File {}: signature validation failed; report {}", fileUUID, reports.getXmlSimpleReport());
            throw new LogicException(new Notification(Notification.Type.ERROR, "TODO", "TODO"));
        }
        final List<String> signatureIds = reports.getSimpleReport().getSignatureIdList();
        return reports.getSimpleReport().getBestSignatureTime(signatureIds.get(signatureIds.size() - 1)).toInstant();
    }
}
