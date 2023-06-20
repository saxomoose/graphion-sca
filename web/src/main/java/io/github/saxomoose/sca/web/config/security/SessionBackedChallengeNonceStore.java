package io.github.saxomoose.sca.web.config.security;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import eu.webeid.security.challenge.ChallengeNonce;
import eu.webeid.security.challenge.ChallengeNonceStore;
import io.github.saxomoose.sca.web.enums.SessionEnum;
import io.github.saxomoose.sca.web.models.transfer.pki.SerializableChallengeNonce;
import jakarta.servlet.http.HttpSession;

/**
 * Session-backed challenge nonce store.
 */
@Component
public class SessionBackedChallengeNonceStore implements ChallengeNonceStore {
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
        currentSession().setAttribute(SessionEnum.CHALLENGE_NONCE.toString(), serializableChallengeNonce);
    }

    @Override
    public ChallengeNonce getAndRemoveImpl() 
    {
        final ChallengeNonce challengeNonce = ((SerializableChallengeNonce) currentSession().getAttribute(SessionEnum.CHALLENGE_NONCE.toString())).getchallengeNonce();
        currentSession().removeAttribute(SessionEnum.CHALLENGE_NONCE.toString());
        return challengeNonce;
    }

    private HttpSession currentSession() 
    {
        return httpSessionFactory.getObject();
    }
}
