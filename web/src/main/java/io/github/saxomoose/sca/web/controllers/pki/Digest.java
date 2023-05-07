package io.github.saxomoose.sca.web.controllers.pki;

public class Digest {
    private String hash;
    private String hashFunction;
    
    public String hash()
    {
        return hash;
    }

    public String hashFunction()
    {
        return hashFunction;
    }

    public void setHash(String hash)
    {
        this.hash = hash;
    }

    public void setHashFunction(String hashFunction)
    {
        this.hashFunction = hashFunction;
    }
}
