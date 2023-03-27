package io.github.saxomoose.sca.web.utils;

public class Tuple<X, Y> {
    final X first;
    final Y second;

    public static Tuple<String, String> of(String filename, String path) {
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
