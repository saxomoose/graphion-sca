package io.github.saxomoose.sca.web.config.security;

import java.io.IOException;
import java.io.Serializable;
import java.time.ZonedDateTime;
import eu.webeid.security.challenge.ChallengeNonce;

public class SerializableChallengeNonce implements Serializable {
    private transient ChallengeNonce challengeNonce;

    public SerializableChallengeNonce(ChallengeNonce challengeNonce) 
    {
        this.challengeNonce = challengeNonce;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException 
    {
        out.defaultWriteObject();
        out.writeObject(challengeNonce.getBase64EncodedNonce());
        out.writeObject(challengeNonce.getExpirationTime());
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException 
    {
        in.defaultReadObject();
        challengeNonce = new ChallengeNonce((String) in.readObject(), (ZonedDateTime) in.readObject());
    }

    public ChallengeNonce getchallengeNonce() 
    {
        return challengeNonce;
    }
}
