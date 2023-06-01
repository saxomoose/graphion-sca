package io.github.saxomoose.sca.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import eu.europa.esig.dss.validation.reports.Reports;

@Service
public class PKIValidationService {
    private final CertificateVerifier certificateVerifier;

    @Autowired
    public PKIValidationService(CertificateVerifier certificateVerifier)
    {
        this.certificateVerifier = certificateVerifier;
    }

    public void validateSignature(DSSDocument signedDocument)
    {
        final var documentValidator = SignedDocumentValidator.fromDocument(signedDocument);
        documentValidator.setCertificateVerifier(certificateVerifier);
        documentValidator.setValidationLevel(ValidationLevel.ARCHIVAL_DATA);
        final Reports reports = documentValidator.validateDocument();
        final var simpleReport = reports.getSimpleReport();
    }
}
