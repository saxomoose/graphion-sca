package io.github.saxomoose.sca.web.utils;

public class Tuple<X, Y> {
    private final X first;
    private final Y second;

    public static Tuple<String, String> documentArgs(String filename, String path) {
        return new Tuple<>(filename, path);
    }

    private Tuple(X first, Y second){
        this.first = first;
        this.second = second;
    }

    // documentArgs
    public String filename() {
        return (String) first;
    }
    public String path() {
        return (String) second;
    }
}
