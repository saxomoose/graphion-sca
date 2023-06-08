package io.github.saxomoose.sca.web.services;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import eu.europa.esig.dss.validation.reports.Reports;
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

    public Boolean validateSignature(final File workbenchFile) throws SignatureException
    {
        final var documentValidator = SignedDocumentValidator.fromDocument(new FileDocument(workbenchFile));
        documentValidator.setCertificateVerifier(certificateVerifier);
        documentValidator.setValidationLevel(ValidationLevel.LONG_TERM_DATA);
        final Reports reports = documentValidator.validateDocument();
        if (reports.getSimpleReport().getSignaturesCount() == reports.getSimpleReport().getValidSignaturesCount()) {
            return Boolean.TRUE;
        } else {
            logger.error("Signature validation failed: {}", reports.getXmlSimpleReport());
            throw new SignatureException(new Notification(
                Notification.Type.ERROR,
                "TODO",
                "TODO"));
        }
    }
}
