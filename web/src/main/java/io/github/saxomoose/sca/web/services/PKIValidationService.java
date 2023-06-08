package io.github.saxomoose.sca.web.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import eu.europa.esig.dss.validation.reports.Reports;
import io.github.saxomoose.sca.web.exceptions.BusinessLogicException;
import io.github.saxomoose.sca.web.exceptions.SignatureException;
import io.github.saxomoose.sca.web.utils.Notification;

@Service
public class PKIValidationService {
    private static final Logger logger = LoggerFactory.getLogger(RedisSubService.class);

    private final CertificateVerifier certificateVerifier;

    @Autowired
    public PKIValidationService(CertificateVerifier certificateVerifier)
    {
        this.certificateVerifier = certificateVerifier;
    }

    public Instant validateSignature(final UUID fileUUID, final DSSDocument signedDocument) throws BusinessLogicException
    {
        final var documentValidator = SignedDocumentValidator.fromDocument(signedDocument);
        documentValidator.setCertificateVerifier(certificateVerifier);
        documentValidator.setValidationLevel(ValidationLevel.LONG_TERM_DATA);
        final Reports reports = documentValidator.validateDocument();
        if (!(reports.getSimpleReport().getSignaturesCount() == reports.getSimpleReport().getValidSignaturesCount())) {
            logger.error("File {}: signature validation failed; report {}", fileUUID, reports.getXmlSimpleReport());
            throw new SignatureException(new Notification(Notification.Type.ERROR, "TODO", "TODO"));
        }
        final List<String> signatureIds = reports.getSimpleReport().getSignatureIdList();
        return reports.getSimpleReport().getBestSignatureTime(signatureIds.get(signatureIds.size() - 1)).toInstant();
    }
}
