package io.github.saxomoose.sca.web.controllers.pki;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import eu.webeid.security.challenge.ChallengeNonceGenerator;

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
        final ChallengePayload challenge = new ChallengePayload();
        challenge.setNonce(challengeNonceGenerator.generateAndStoreNonce().getBase64EncodedNonce());
        return ResponseEntity
            .ok()
            .body(challenge);
    }
}
