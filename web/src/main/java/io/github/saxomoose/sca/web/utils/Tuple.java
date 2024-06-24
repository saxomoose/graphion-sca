package io.github.saxomoose.sca.web.utils;

public class Tuple<X, Y> {
    private final X first;
    private final Y second;

    public Tuple(X first, Y second)
    {
        this.first = first;
        this.second = second;
    }

    public X first()
    {
        return first;
    }

    public Y second()
    {
        return second;
    }
}
