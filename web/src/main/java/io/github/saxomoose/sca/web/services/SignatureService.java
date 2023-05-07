package io.github.saxomoose.sca.web.services;

import org.springframework.stereotype.Service;

import io.github.saxomoose.sca.web.controllers.pki.Digest;
import io.github.saxomoose.sca.web.controllers.pki.SignatureCertificate;

@Service
public class SignatureService {

    public Digest prepareContainer(SignatureCertificate signatureCertificate) {
        return new Digest();
    }
    
}
