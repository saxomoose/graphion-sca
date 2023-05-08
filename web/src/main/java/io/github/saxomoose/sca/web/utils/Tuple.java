package io.github.saxomoose.sca.web.utils;

import java.util.UUID;

public class Tuple<X, Y> {
    final X first;
    final Y second;

    public static Tuple<String, UUID> of(String filename, UUID fileUUID)
    {
        return new Tuple<>(filename, fileUUID);
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

}
