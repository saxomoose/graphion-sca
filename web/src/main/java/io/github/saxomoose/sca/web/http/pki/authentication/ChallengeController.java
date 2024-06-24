package io.github.saxomoose.sca.web.http.pki.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import eu.webeid.security.challenge.ChallengeNonceGenerator;
import io.github.saxomoose.sca.web.models.transfer.pki.ChallengePayload;

@RestController
public class ChallengeController {
    private final ChallengeNonceGenerator challengeNonceGenerator;

    @Autowired
    public ChallengeController(ChallengeNonceGenerator challengeNonceGenerator) 
    {
        this.challengeNonceGenerator = challengeNonceGenerator;
    }

    /**
     * REST endpoint that issues challenge nonces.
     * TODO: Secure endpoint.
     */
    @GetMapping(
        value = "/pki/challenge",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChallengePayload> challenge() 
    {
        ChallengePayload challenge = new ChallengePayload();
        challenge.setNonce(challengeNonceGenerator.generateAndStoreNonce().getBase64EncodedNonce());
        return ResponseEntity
            .ok()
            .body(challenge);
    }
}
