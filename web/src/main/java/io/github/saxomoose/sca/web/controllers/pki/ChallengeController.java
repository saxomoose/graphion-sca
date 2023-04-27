package io.github.saxomoose.sca.web.controllers.pki;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import eu.webeid.security.challenge.ChallengeNonceGenerator;

public class ChallengeController {
    private final ChallengeNonceGenerator challengeNonceGenerator;

    @Autowired
    public ChallengeController(ChallengeNonceGenerator challengeNonceGenerator) {
        this.challengeNonceGenerator = challengeNonceGenerator;
    }

    /**
     * REST endpoint that issues challenge nonces.
     */
    @GetMapping("challenge")
    public Challenge challenge() {
        final var challenge = new Challenge();
        challenge.setNonce(challengeNonceGenerator.generateAndStoreNonce().getBase64EncodedNonce());
        return challenge;
    }
}
