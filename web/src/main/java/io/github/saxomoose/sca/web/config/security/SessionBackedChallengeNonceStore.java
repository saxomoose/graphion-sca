package io.github.saxomoose.sca.web.config.security;

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
    public SessionBackedChallengeNonceStore(ObjectFactory<HttpSession> httpSessionFactory) 
    {
        this.httpSessionFactory = httpSessionFactory;
    }

    @Override
    public void put(ChallengeNonce challengeNonce) 
    {
        final var serializableChallengeNonce = new SerializableChallengeNonce(challengeNonce);
        currentSession().setAttribute(CHALLENGE_NONCE_KEY, serializableChallengeNonce);
    }

    @Override
    public ChallengeNonce getAndRemoveImpl() 
    {
        final ChallengeNonce challengeNonce = ((SerializableChallengeNonce) currentSession().getAttribute(CHALLENGE_NONCE_KEY)).getchallengeNonce();
        currentSession().removeAttribute(CHALLENGE_NONCE_KEY);
        return challengeNonce;
    }

    private HttpSession currentSession() 
    {
        return httpSessionFactory.getObject();
    }
}
