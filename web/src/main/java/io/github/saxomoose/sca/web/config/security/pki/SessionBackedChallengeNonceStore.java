package io.github.saxomoose.sca.web.config.security.pki;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import eu.webeid.security.challenge.ChallengeNonce;
import eu.webeid.security.challenge.ChallengeNonceStore;
import jakarta.servlet.http.HttpSession;

/**
 * Session-backed challenge nonce store.
 */
@Component
public class SessionBackedChallengeNonceStore implements ChallengeNonceStore {
    private static final String CHALLENGE_NONCE_KEY = "challenge-nonce";

    private final ObjectFactory<HttpSession> httpSessionFactory;

    @Autowired
    public SessionBackedChallengeNonceStore(ObjectFactory<HttpSession> httpSessionFactory) {
        this.httpSessionFactory = httpSessionFactory;
    }

    @Override
    public void put(ChallengeNonce challengeNonce) {
        currentSession().setAttribute(CHALLENGE_NONCE_KEY, challengeNonce);
    }

    @Override
    public ChallengeNonce getAndRemoveImpl() {
        final var challengeNonce =
                (ChallengeNonce) currentSession().getAttribute(CHALLENGE_NONCE_KEY);
        currentSession().removeAttribute(CHALLENGE_NONCE_KEY);
        return challengeNonce;
    }

    private HttpSession currentSession() {
        return httpSessionFactory.getObject();
    }
}
