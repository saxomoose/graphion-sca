package io.github.saxomoose.sca.web.controllers.pki;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.saxomoose.sca.web.services.SignatureService;

@RestController
public class SignatureController {

    private final SignatureService signatureService;

    public SignatureController(SignatureService signatureService) {
        this.signatureService = signatureService;
    }


    @PostMapping(value = "/pki/sign/prepare", produces = "application/json")
    public void prepare(
        @RequestBody final SignatureCertificate signatureCertificate
    )
    {
        signatureService.prepareContainer(signatureCertificate);
    }

    @PostMapping(value = "/pki/sign/sign", produces = "application/json")
    public void sign()
    {

    }
}
