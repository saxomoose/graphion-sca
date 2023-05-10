package io.github.saxomoose.sca.web.utils;

import java.util.UUID;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import io.github.saxomoose.sca.web.controllers.pki.Digest;

public class Tuple<X, Y> {
    final X first;
    final Y second;

    public static Tuple<String, UUID> of(String filename, UUID fileUUID)
    {
        return new Tuple<>(filename, fileUUID);
    }

    public static Tuple<Digest, PAdESSignatureParameters> of(Digest digest, PAdESSignatureParameters parameters)
    {
        return new Tuple<>(digest, parameters);
    }

    private Tuple(X first, Y second)
    {
        this.first = first;
        this.second = second;
    }

    // documentArgs
    public String filename()
    {
        return (String) first;
    }

    public UUID fileUUID()
    {
        return (UUID) second;
    }

    public Digest digest()
    {
        return (Digest) first;
    }

    public PAdESSignatureParameters parameters()
    {
        return (PAdESSignatureParameters) second;
    }

}
